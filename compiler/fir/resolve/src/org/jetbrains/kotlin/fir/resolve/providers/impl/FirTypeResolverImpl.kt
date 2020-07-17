/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.providers.impl

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.resolve.FirTypeResolver
import org.jetbrains.kotlin.fir.resolve.constructType
import org.jetbrains.kotlin.fir.resolve.firSymbolProvider
import org.jetbrains.kotlin.fir.resolve.qualifierResolver
import org.jetbrains.kotlin.fir.scopes.FirScope
import org.jetbrains.kotlin.fir.scopes.processClassifiersByName
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassifierSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.types.impl.ConeClassLikeTypeImpl
import org.jetbrains.kotlin.fir.types.impl.FirImplicitBuiltinTypeRef
import org.jetbrains.kotlin.name.ClassId

class FirTypeResolverImpl(private val session: FirSession) : FirTypeResolver {

    private val symbolProvider by lazy {
        session.firSymbolProvider
    }

    private data class ClassIdInSession(val session: FirSession, val id: ClassId)

    private val implicitBuiltinTypeSymbols = mutableMapOf<ClassIdInSession, FirClassLikeSymbol<*>>()

    // TODO: get rid of session used here, and may be also of the cache above (see KT-30275)
    private fun resolveBuiltInQualified(id: ClassId, session: FirSession): FirClassLikeSymbol<*> {
        val nameInSession = ClassIdInSession(session, id)
        return implicitBuiltinTypeSymbols.getOrPut(nameInSession) {
            symbolProvider.getClassLikeSymbolByFqName(id)!!
        }
    }

    override fun resolveToSymbol(
        typeRef: FirTypeRef,
        scope: FirScope
    ): FirClassifierSymbol<*>? {
        return when (typeRef) {
            is FirResolvedTypeRef -> typeRef.coneTypeSafe<ConeLookupTagBasedType>()?.lookupTag?.let(symbolProvider::getSymbolByLookupTag)

            is FirUserTypeRef -> {
                val qualifierResolver = session.qualifierResolver
                var resolvedSymbol: FirClassifierSymbol<*>? = null
                scope.processClassifiersByName(typeRef.qualifier.first().name) { symbol ->
                    if (resolvedSymbol != null) return@processClassifiersByName
                    resolvedSymbol = when (symbol) {
                        is FirClassLikeSymbol<*> -> {
                            if (typeRef.qualifier.size == 1) {
                                symbol
                            } else {
                                qualifierResolver.resolveSymbolWithPrefix(typeRef.qualifier, symbol.classId)
                            }
                        }
                        is FirTypeParameterSymbol -> {
                            assert(typeRef.qualifier.size == 1)
                            symbol
                        }
                        else -> error("!")
                    }
                }

                // TODO: Imports
                resolvedSymbol ?: qualifierResolver.resolveSymbol(typeRef.qualifier)
            }

            is FirImplicitBuiltinTypeRef -> {
                resolveBuiltInQualified(typeRef.id, session)
            }

            else -> null
        }
    }

    private fun resolveUserType(typeRef: FirUserTypeRef, symbol: FirClassifierSymbol<*>?): ConeKotlinType {
        if (symbol == null) {
            return ConeKotlinErrorType("Symbol not found, for `${typeRef.render()}`")
        }
        return symbol.constructType(typeRef.qualifier, typeRef.isMarkedNullable, symbolOriginSession = session, typeRef.annotations.computeTypeAttributes())
    }

    private fun createFunctionalType(typeRef: FirFunctionTypeRef): ConeClassLikeType {
        val parameters =
            listOfNotNull(typeRef.receiverTypeRef?.coneType) +
                    typeRef.valueParameters.map { it.returnTypeRef.coneType } +
                    listOf(typeRef.returnTypeRef.coneType)
        val classId = if (typeRef.isSuspend) {
            KotlinBuiltIns.getSuspendFunctionClassId(typeRef.parametersCount)
        } else {
            KotlinBuiltIns.getFunctionClassId(typeRef.parametersCount)
        }
        val attributes = typeRef.annotations.computeTypeAttributes()
        return ConeClassLikeTypeImpl(
            resolveBuiltInQualified(classId, session).toLookupTag(),
            parameters.toTypedArray(),
            typeRef.isMarkedNullable,
            attributes
        )
    }

    override fun resolveType(
        typeRef: FirTypeRef,
        scope: FirScope
    ): ConeKotlinType {
        return when (typeRef) {
            is FirResolvedTypeRef -> typeRef.type
            is FirUserTypeRef -> resolveUserType(typeRef, resolveToSymbol(typeRef, scope))
            is FirFunctionTypeRef -> createFunctionalType(typeRef)
            is FirDelegatedTypeRef -> resolveType(typeRef.typeRef, scope)
            is FirDynamicTypeRef -> ConeKotlinErrorType("Not supported: ${typeRef::class.simpleName}")
            else -> error("!")
        }
    }
}
