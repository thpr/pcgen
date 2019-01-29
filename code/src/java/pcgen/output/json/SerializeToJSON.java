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

import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SimpleMovement;
import pcgen.core.Vision;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializeToJSON
{

	public static void serializePC(PlayerCharacter pc)
	{
		GsonBuilder builder = new GsonBuilder();

		builder.registerTypeAdapter(Race.class, new RaceSerializer());
		builder.registerTypeAdapter(Vision.class, new VisionSerializer());
		builder.registerTypeAdapter(SimpleMovement.class,
			new MovementSerializer());
		builder.registerTypeAdapter(SerializerUtilities.LISTTYPE_RACESUBTYPE,
			new RaceSubTypeSerializer());
		builder.registerTypeAdapter(SerializerUtilities.LISTTYPE_TYPE,
			new TypeSerializer());
		builder.registerTypeAdapter(SerializerUtilities.LISTTYPE_SIMPLEMOVEMENT,
			new SimpleMovementSerializer());
		builder.registerTypeAdapter(PlayerCharacter.class,
			new PlayerCharacterSerializer());
		Gson gson = builder.setPrettyPrinting().create();
		String json = gson.toJson(pc);
		System.err.println(json);
	}
}
