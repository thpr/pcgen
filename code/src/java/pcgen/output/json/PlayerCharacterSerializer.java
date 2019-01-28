package pcgen.output.json;

import java.lang.reflect.Type;

import pcgen.core.PlayerCharacter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PlayerCharacterSerializer
		implements JsonSerializer<PlayerCharacter>
{

	@Override
	public JsonElement serialize(PlayerCharacter pc, Type typeOfSrc,
		JsonSerializationContext context)
	{
		try
		{
			SerializerContext.pcContext.set(pc);
			return writeDetail(pc, context);
		}
		finally
		{
			SerializerContext.pcContext.remove();
		}
	}

	private JsonElement writeDetail(PlayerCharacter pc,
		JsonSerializationContext context)
	{
		JsonObject pcJson = new JsonObject();
		pcJson.addProperty("name", pc.getName());
		pcJson.add("race", context.serialize(pc.getRace()));
		return pcJson;
	}

}
