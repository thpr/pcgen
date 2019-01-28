package pcgen.output.json;

import pcgen.core.Movement;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Vision;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializeToJSON
{

	public static void serializePC(PlayerCharacter pc)
	{
		GsonBuilder builder = new GsonBuilder();

		builder.registerTypeAdapter(Race.class, new RaceSerializer());
		builder.registerTypeAdapter(Movement.class, new MovementSerializer());
		builder.registerTypeAdapter(Vision.class, new VisionSerializer());
		builder.registerTypeAdapter(PlayerCharacter.class,
			new PlayerCharacterSerializer());
		Gson gson = builder.setPrettyPrinting().create();
		String json = gson.toJson(pc);
		System.err.println(json);
	}
}
