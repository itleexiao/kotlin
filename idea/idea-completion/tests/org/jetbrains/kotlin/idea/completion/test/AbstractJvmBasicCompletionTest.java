/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.completion.test;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.test.KotlinWithJdkAndRuntimeLightProjectDescriptor;
import org.jetbrains.kotlin.resolve.DefaultBuiltInPlatforms;
import org.jetbrains.kotlin.resolve.TargetPlatform;
import org.jetbrains.kotlin.resolve.JvmPlatform;

public abstract class AbstractJvmBasicCompletionTest extends KotlinFixtureCompletionBaseTestCase {
    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return KotlinWithJdkAndRuntimeLightProjectDescriptor.INSTANCE;
    }

    @NotNull
    @Override
    public TargetPlatform getPlatform() {
        return DefaultBuiltInPlatforms.INSTANCE.getJvmPlatform();
    }

    @NotNull
    @Override
    protected CompletionType defaultCompletionType() {
        return CompletionType.BASIC;
    }
}
