/*
 * Copyright 2014-2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.editor.plugin;

import com.kotcrab.vis.editor.module.Module;

/**
 * Interface allowing to inject custom modules into VisEditor modules containers
 * @param <T> type of base module depending on container that you want to inject your module into
 * @author Kotcrab
 */
public interface ContainerExtension<T extends Module> {
	enum ExtensionScope {EDITOR, PROJECT, SCENE, PHYSICS_EDITOR}

	ExtensionScope getScope ();
}
