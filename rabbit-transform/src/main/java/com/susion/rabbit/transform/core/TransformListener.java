package com.susion.rabbit.transform.core;

import com.susion.rabbit.transform.core.context.TransformContext;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the transformStreamToArray lifecycle listener
 */
public interface TransformListener {

    default void onPreTransform(@NotNull final TransformContext context) {
    }

    default void onPostTransform(@NotNull final TransformContext context) {
    }

}
