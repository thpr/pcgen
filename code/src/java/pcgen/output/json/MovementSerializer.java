package pcgen.output.json;

import java.lang.reflect.Type;

import pcgen.core.Movement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MovementSerializer implements JsonSerializer<Movement>
{

	@Override
	public JsonElement serialize(Movement movement, Type typeOfSrc,
		JsonSerializationContext context)
	{
		JsonObject jsonMovement = new JsonObject();
		int flag = movement.getMoveRatesFlag();
		if (flag == 0)
		{
			JsonArray jsonMoveInfo = new JsonArray();
			for (int i = 0; i < movement.getNumberOfMovements(); i++)
			{
				String type = movement.getMovementType(i);
				{
					JsonObject movementField = new JsonObject();
					movementField.addProperty(type, movement.getMovement(i));
					jsonMoveInfo.add(movementField);
				}
			}
			jsonMovement.add("moveInfo", jsonMoveInfo);
		}
		else // flag is 2
		{
			jsonMovement.addProperty("cloneSource",
				movement.getMovementType(0));
			jsonMovement.addProperty("cloneTarget",
				movement.getMovementType(1));
			String operator = movement.getMovementMultOp(1);
			jsonMovement.addProperty("operator", operator);
			if (operator.isBlank())
			{
				jsonMovement.addProperty("modValue", movement.getMovement(1));
			}
			else
			{
				jsonMovement.addProperty("modValue",
					movement.getMovementMult(1));
			}
		}

		return jsonMovement;
	}
}
