/*
 * Copyright 2019 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.output.json;

import java.lang.reflect.Type;

import pcgen.base.formula.Formula;
import pcgen.core.Vision;
import pcgen.util.enumeration.VisionType;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class VisionSerializer implements JsonSerializer<Vision>
{
	@Override
	public JsonElement serialize(Vision vision, Type typeOfSrc,
		JsonSerializationContext context)
	{
		JsonObject jsonVision = new JsonObject();
		VisionType visionType = vision.getType();
		Formula distance = vision.getDistance();
		jsonVision.addProperty("type", visionType.toString());
		jsonVision.addProperty("distance", distance.toString());
		CDOMSerializer.writePreReq(jsonVision, vision);
		return jsonVision;
	}
}
