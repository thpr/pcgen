package pcgen.output.json;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;

public class SerializerUtilities
{

	static void writeList(JsonGenerator jsonGenerator, String fieldName,
		List<?> list) throws IOException
	{
		if ((list != null) && !list.isEmpty())
		{
			jsonGenerator.writeArrayFieldStart(fieldName);
			for (Object obj : list)
			{
				jsonGenerator.writeObject(obj);
			}
			jsonGenerator.writeEndArray();
		}
	}

	public static void writeStringFieldIfNotBlank(JsonGenerator jsonGenerator,
		String name, String value) throws IOException
	{
		if ((value != null) && !value.isBlank())
		{
			jsonGenerator.writeStringField(name, value);
		}
	}

	public static void writeObjectFieldIfNotNull(JsonGenerator jsonGenerator,
		String name, Object value) throws IOException
	{
		if (value != null)
		{
			jsonGenerator.writeObjectField(name, value);
		}
	}

	public static void writeFieldIfPresent(JsonGenerator jsonGenerator,
		String name, Optional<?> optional) throws IOException
	{
		if (optional.isPresent())
		{
			jsonGenerator.writeObjectField(name, optional.get());
		}
	}

}
