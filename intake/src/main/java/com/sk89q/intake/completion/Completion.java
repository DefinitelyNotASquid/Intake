/*
 * Intake, a command processing library
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) Intake team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.intake.completion;

import com.google.common.annotations.Beta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a method to provide a {@link CommandCompleter} instance.
 */
@Beta
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Completion {

    String DEFAULT_ID = "default";

    /**
     * The completer class.
     *
     * @return the completer class
     */
    Class<? extends CommandCompleter> value();

    /**
     * The id for this completer.
     *
     * <p>If the id is empty, "default" will be used instead.</p>
     *
     * @return the id
     */
    String id() default "default";

    /**
     * Test whether we should use a shared instance of the completer class,
     * instead of creating a new one.
     *
     * <p>It is recommended to use shared instances where possible.</p>
     *
     * @return if we should use a shared instance of the completer class
     */
    boolean useSharedInstance() default false;
}
