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

import pcgen.cdom.enumeration.RaceSubType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class RaceSubTypeSerializer implements JsonSerializer<List<RaceSubType>>
{

	@Override
	public JsonElement serialize(List<RaceSubType> raceSubTypes, Type typeOfSrc,
		JsonSerializationContext context)
	{
		JsonArray jsonRaceSubTypes = new JsonArray();
		for (RaceSubType subType : raceSubTypes)
		{
			jsonRaceSubTypes.add(subType.toString());
		}
		return jsonRaceSubTypes;
	}
}
