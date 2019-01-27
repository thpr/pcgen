package pcgen.output.json;

import pcgen.core.PlayerCharacter;

public class SerializerContext
{

	public static final ThreadLocal<PlayerCharacter> pcContext =
			new ThreadLocal<>();

}
