package com.susion.rabbit.gradle.core.rxentension

import com.android.build.gradle.BaseExtension
import com.android.repository.Revision
import org.gradle.api.Project

/**
 * Returns android extension
 *
 * @author johnsonlee
 */
inline fun <reified T : BaseExtension> Project.getAndroid(): T = extensions.getByName("android") as T

/**
 * The gradle version
 */
val Project.gradleVersion: Revision
    get() = Revision.parseRevision(gradle.gradleVersion)
