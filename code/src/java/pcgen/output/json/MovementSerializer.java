package pcgen.output.json;

import java.io.IOException;

import pcgen.core.Movement;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class MovementSerializer extends StdSerializer<Movement>
{
	public MovementSerializer()
	{
		this(null);
	}

	public MovementSerializer(Class<Movement> t)
	{
		super(t);
	}

	@Override
	public void serialize(Movement movement, JsonGenerator jsonGenerator,
		SerializerProvider serializer) throws IOException
	{
		//Note jsonGenerator.getOutputContext() can be used to determine whether to write items beyond the identifier

		jsonGenerator.writeStartObject();
		int flag = movement.getMoveRatesFlag();
		if (flag == 0)
		{
			jsonGenerator.writeArrayFieldStart("moveInfo");
			for (int i = 0; i < movement.getNumberOfMovements(); i++)
			{
				String type = movement.getMovementType(i);
				{
					jsonGenerator.writeStartObject();
					jsonGenerator.writeNumberField(type,
						movement.getMovement(i));
					jsonGenerator.writeEndObject();
				}
			}
			jsonGenerator.writeEndArray();
		}
		else // flag is 2
		{
			jsonGenerator.writeStringField("cloneSource",
				movement.getMovementType(0));
			jsonGenerator.writeStringField("cloneTarget",
				movement.getMovementType(1));
			String operator = movement.getMovementMultOp(1);
			jsonGenerator.writeStringField("operator", operator);
			if (operator.isBlank())
			{
				jsonGenerator.writeNumberField("modValue",
					movement.getMovement(1));
			}
			else
			{
				jsonGenerator.writeNumberField("modValue",
					movement.getMovementMult(1));
			}
		}

		jsonGenerator.writeEndObject();
	}
}
