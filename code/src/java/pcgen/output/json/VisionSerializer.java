package pcgen.output.json;

import java.io.IOException;

import pcgen.base.formula.Formula;
import pcgen.core.Vision;
import pcgen.util.enumeration.VisionType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class VisionSerializer extends StdSerializer<Vision>
{
	public VisionSerializer()
	{
		this(null);
	}

	public VisionSerializer(Class<Vision> t)
	{
		super(t);
	}

	@Override
	public void serialize(Vision vision, JsonGenerator jsonGenerator,
		SerializerProvider serializer) throws IOException
	{
		//Note jsonGenerator.getOutputContext() can be used to determine whether to write items beyond the identifier

		jsonGenerator.writeStartObject();
		VisionType visionType = vision.getType();
		jsonGenerator.writeStringField("type", visionType.toString());
		Formula distance = vision.getDistance();
		jsonGenerator.writeStringField("distance", distance.toString());
		CDOMSerializer.writePreReq(jsonGenerator, vision);
		jsonGenerator.writeEndObject();
	}
}
