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
import java.util.List;
import java.util.Optional;

import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.SimpleMovement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;

public class SerializerUtilities
{

	static void writeList(JsonObject jsonObject, String fieldName, List<?> list,
		JsonSerializationContext context)
	{
		if ((list != null) && !list.isEmpty())
		{
			JsonArray jsonArray = new JsonArray();
			for (Object obj : list)
			{
				jsonArray.add(context.serialize(obj));
			}
			jsonObject.add(fieldName, jsonArray);
		}
	}

	public static void writeStringFieldIfNotBlank(JsonObject jsonObject,
		String name, String value)
	{
		if ((value != null) && !value.isBlank())
		{
			jsonObject.addProperty(name, value);
		}
	}

	public static void writeObjectFieldIfNotNull(JsonObject jsonObject,
		String name, Object value, JsonSerializationContext context)
	{
		if (value != null)
		{
			jsonObject.add(name, context.serialize(value));
		}
	}

	public static void writeFieldIfPresent(JsonObject jsonObject, String name,
		Optional<?> optional, JsonSerializationContext context)
	{
		optional.ifPresent(
			object -> jsonObject.add(name, context.serialize(object)));
	}

	public static final Type LISTTYPE_RACESUBTYPE = new TypeToken<List<RaceSubType>>() {}.getType();
	public static final Type LISTTYPE_TYPE = new TypeToken<List<pcgen.cdom.enumeration.Type>>() {}.getType();
	public static final Type LISTTYPE_SIMPLEMOVEMENT = new TypeToken<List<SimpleMovement>>() {}.getType();

}
