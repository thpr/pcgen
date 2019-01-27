package pcgen.output.json;

import java.io.IOException;

import pcgen.core.PlayerCharacter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PlayerCharacterSerializer extends StdSerializer<PlayerCharacter>
{

	public PlayerCharacterSerializer()
	{
		this(null);
	}

	public PlayerCharacterSerializer(Class<PlayerCharacter> t)
	{
		super(t);
	}

	@Override
	public void serialize(PlayerCharacter pc, JsonGenerator jsonGenerator,
		SerializerProvider serializer) throws IOException
	{
		//Note jsonGenerator.getOutputContext() can be used to determine whether to write items beyond the identifier

		try
		{
			SerializerContext.pcContext.set(pc);
			writeDetail(pc, jsonGenerator, serializer);
		}
		finally
		{
			SerializerContext.pcContext.remove();
		}
	}

	private void writeDetail(PlayerCharacter pc, JsonGenerator jsonGenerator,
		SerializerProvider serializer) throws IOException
	{
		jsonGenerator.writeStartObject();
		jsonGenerator.writeStringField("name", pc.getName());
		jsonGenerator.writeObjectField("race", pc.getRace());

		jsonGenerator.writeEndObject();
	}

}
