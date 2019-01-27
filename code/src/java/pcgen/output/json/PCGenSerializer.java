package pcgen.output.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

public class PCGenSerializer extends BeanSerializerFactory
{

	protected PCGenSerializer(SerializerFactoryConfig config)
	{
		super(config);
	}

	@Override
	public JsonSerializer<Object> createSerializer(SerializerProvider prov,
		JavaType origType) throws JsonMappingException
	{
		JsonSerializer<Object> candidate =
				super.createSerializer(prov, origType);
		if (candidate instanceof BeanSerializer && !origType.getRawClass().getPackageName().startsWith("java."))
		{
			throw new RuntimeException("Cannot write: " + origType);
		}
		return candidate;
	}

	@Override
	public SerializerFactory withConfig(SerializerFactoryConfig config)
	{
		return new PCGenSerializer(config);
	}
	
	
}
