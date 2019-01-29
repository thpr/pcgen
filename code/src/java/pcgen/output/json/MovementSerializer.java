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

import pcgen.core.SimpleMovement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MovementSerializer implements JsonSerializer<SimpleMovement>
{

	@Override
	public JsonElement serialize(SimpleMovement movement, Type typeOfSrc,
		JsonSerializationContext context)
	{
		JsonObject jsonMovement = new JsonObject();
		jsonMovement.addProperty("movementType",
			movement.getMovementType().toString());
		jsonMovement.addProperty("rate", movement.getMovement());
		return jsonMovement;
	}
}
