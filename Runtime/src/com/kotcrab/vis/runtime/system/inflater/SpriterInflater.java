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

package com.kotcrab.vis.runtime.system.inflater;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.assets.AssetManager;
import com.kotcrab.vis.runtime.assets.SpriterAsset;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.SpriterComponent;
import com.kotcrab.vis.runtime.component.SpriterProtoComponent;
import com.kotcrab.vis.runtime.util.SpriterData;

/** @author Kotcrab */
@Wire
public class SpriterInflater extends Manager {
	private ComponentMapper<SpriterProtoComponent> protoCm;
	private ComponentMapper<AssetComponent> assetCm;

	private Entity flyweight;

	private EntityTransmuter transmuter;

	private AssetManager manager;

	public SpriterInflater (AssetManager manager) {
		this.manager = manager;
	}

	@Override
	protected void initialize () {
		EntityTransmuterFactory factory = new EntityTransmuterFactory(world).remove(SpriterProtoComponent.class);
		transmuter = factory.build();
	}

	@Override
	protected void setWorld (World world) {
		super.setWorld(world);
		flyweight = Entity.createFlyweight(world);
	}

	@Override
	public void added (int entityId) {
		flyweight.id = entityId;
		if (protoCm.has(entityId) == false) return;

		AssetComponent assetComponent = assetCm.get(entityId);
		SpriterProtoComponent protoComponent = protoCm.get(entityId);

		SpriterAsset asset = (SpriterAsset) assetComponent.asset;
		SpriterData data = manager.get(asset.getPath(), SpriterData.class);
		if (data == null)
			throw new IllegalStateException("Can't load scene, spriter data is missing: " + asset.getPath());
		SpriterComponent component = new SpriterComponent(data.loader, data.data, protoComponent.scale);

		protoComponent.fill(component);

		transmuter.transmute(flyweight);
		flyweight.edit().add(component);
	}
}
