package pcgen.output.json;

import pcgen.core.Movement;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Vision;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;

public class SerializeToJSON
{

	public static void serializePC(PlayerCharacter pc)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializerFactory(new PCGenSerializer(
			BeanSerializerFactory.instance.getFactoryConfig()));
		SimpleModule module = new SimpleModule();
		module.addSerializer(Race.class, new RaceSerializer());
		module.addSerializer(Movement.class, new MovementSerializer());
		module.addSerializer(Vision.class, new VisionSerializer());
		module.addSerializer(PlayerCharacter.class, new PlayerCharacterSerializer());
		objectMapper.registerModule(module);
		try
		{
			String pcAsString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pc);
			System.err.println(pcAsString);
		}
		catch (JsonProcessingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
