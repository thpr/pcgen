package pcgen.output.json;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.AllowUtilities;
import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.DescriptionFormatting;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.util.StringPClassUtil;

import com.fasterxml.jackson.core.JsonGenerator;

public class CDOMSerializer
{

	public static void addStandardItems(JsonGenerator jsonGenerator,
		PObject cdo) throws IOException
	{
		addIdentifier(jsonGenerator, cdo);
		jsonGenerator.writeStringField("displayName", cdo.getDisplayName());
		SerializerUtilities.writeFieldIfPresent(jsonGenerator, "localScopeName",
			cdo.getLocalScopeName());
		jsonGenerator.writeBooleanField("isInternal", cdo.isInternal());
		jsonGenerator.writeBooleanField("nameIsPI", cdo.isNamePI());
		jsonGenerator.writeBooleanField("descIsPI",
			cdo.getSafe(ObjectKey.DESC_PI));
		jsonGenerator.writeStringField("outputName", cdo.getOutputName());
		Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
		if (campaign != null)
		{
			jsonGenerator.writeStringField("sourceCampaign",
				campaign.getKeyName());
		}
		jsonGenerator.writeObjectField("sourceURI", cdo.getSourceURI());
		SerializerUtilities.writeStringFieldIfNotBlank(jsonGenerator,
			"sourceWeb", cdo.get(StringKey.SOURCE_WEB));
		SerializerUtilities.writeStringFieldIfNotBlank(jsonGenerator,
			"sourceShort", cdo.get(StringKey.SOURCE_SHORT));
		SerializerUtilities.writeStringFieldIfNotBlank(jsonGenerator,
			"sourceLong", cdo.get(StringKey.SOURCE_LONG));
		SerializerUtilities.writeStringFieldIfNotBlank(jsonGenerator,
			"sourcePage", cdo.get(StringKey.SOURCE_PAGE));
		SerializerUtilities.writeStringFieldIfNotBlank(jsonGenerator,
			"sourceLink", cdo.get(StringKey.SOURCE_LINK));
		SerializerUtilities.writeStringFieldIfNotBlank(jsonGenerator,
			"sourceWeb", cdo.get(StringKey.SOURCE_WEB));
		SerializerUtilities.writeObjectFieldIfNotNull(jsonGenerator,
			"sourceDate", cdo.get(ObjectKey.SOURCE_DATE));

		SerializerUtilities.writeStringFieldIfNotBlank(jsonGenerator, "sortKey",
			cdo.get(StringKey.SORT_KEY));

		//END to-do
		SerializerUtilities.writeList(jsonGenerator, "type",
			cdo.getTrueTypeList(true));
		writeFactSets(jsonGenerator, cdo);
		writeFacts(jsonGenerator, cdo);
	}

	public static void addIdentifier(JsonGenerator jsonGenerator, PObject cdo)
		throws IOException
	{
		jsonGenerator.writeStringField("format",
			StringPClassUtil.getStringFor(cdo.getClass()));
		jsonGenerator.writeStringField("key", cdo.getKeyName());
	}

	private static void writeFacts(JsonGenerator jsonGenerator, PObject cdo)
		throws IOException
	{
		Set<FactKey<?>> keys = cdo.getFactKeys();
		if (keys.isEmpty())
		{
			return;
		}
		jsonGenerator.writeArrayFieldStart("fact");
		for (FactKey<?> factKey : keys)
		{
			processFact(jsonGenerator, cdo, factKey);
		}
		jsonGenerator.writeEndArray();

	}

	private static <T> void processFact(JsonGenerator jsonGenerator,
		PObject cdo, FactKey<T> factKey) throws IOException
	{
		FormatManager<T> formatManager = factKey.getFormatManager();
		Indirect<T> fact = cdo.get(factKey);
		jsonGenerator.writeFieldName(factKey.toString());
		jsonGenerator.writeStringField(formatManager.getIdentifierType(),
			formatManager.unconvert(fact.get()));
	}

	private static void writeFactSets(JsonGenerator jsonGenerator, PObject cdo)
		throws IOException
	{
		Set<FactSetKey<?>> keys = cdo.getFactSetKeys();
		if (keys.isEmpty())
		{
			return;
		}
		jsonGenerator.writeArrayFieldStart("factset");
		for (FactSetKey<?> factSetKey : keys)
		{
			processFactSet(jsonGenerator, cdo, factSetKey);
		}
		jsonGenerator.writeEndArray();
	}

	private static <T> void processFactSet(JsonGenerator jsonGenerator,
		PObject cdo, FactSetKey<T> factSetKey) throws IOException
	{
		FormatManager<T> formatManager = factSetKey.getFormatManager();
		List<Indirect<T>> factSet = cdo.getSetFor(factSetKey);
		jsonGenerator.writeArrayFieldStart(factSetKey.toString());
		String identifier = formatManager.getIdentifierType();
		for (Indirect<T> indirect : factSet)
		{
			jsonGenerator.writeStringField(identifier,
				formatManager.unconvert(indirect.get()));
		}
		jsonGenerator.writeEndArray();
	}

	public static void writePreReq(JsonGenerator jsonGenerator, CDOMObject cdo)
		throws IOException
	{
		if (!cdo.hasPrerequisites())
		{
			return;
		}
		PlayerCharacter pc = SerializerContext.pcContext.get();
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append(PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			cdo.getPrerequisiteList(), false));
		sb.append(AllowUtilities.getAllowInfo(pc, cdo));
		sb.append("</html>");
		jsonGenerator.writeStringField("prerequisites", sb.toString());
	}

	public static void writeDescription(JsonGenerator jsonGenerator,
		PObject cdo) throws IOException
	{
		PlayerCharacter pc = SerializerContext.pcContext.get();
		jsonGenerator.writeStringField("description", DescriptionFormatting
			.piWrapDesc(cdo, pc.getDescription(cdo), false));
	}

	public static void writeList(JsonGenerator jsonGenerator, CDOMObject cdo,
		String name, ListKey<?> key) throws IOException
	{
		SerializerUtilities.writeList(jsonGenerator, name, cdo.getListFor(key));
	}

}
