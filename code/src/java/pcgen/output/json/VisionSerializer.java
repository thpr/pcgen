package pcgen.output.json;

import java.lang.reflect.Type;

import pcgen.base.formula.Formula;
import pcgen.core.Vision;
import pcgen.util.enumeration.VisionType;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class VisionSerializer implements JsonSerializer<Vision>
{
	@Override
	public JsonElement serialize(Vision vision, Type typeOfSrc,
		JsonSerializationContext context)
	{
		JsonObject jsonVision = new JsonObject();
		VisionType visionType = vision.getType();
		Formula distance = vision.getDistance();
		jsonVision.addProperty("type", visionType.toString());
		jsonVision.addProperty("distance", distance.toString());
		CDOMSerializer.writePreReq(jsonVision, vision);
		return jsonVision;
	}
}
