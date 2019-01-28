package pcgen.output.json;

import java.util.List;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

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

}
