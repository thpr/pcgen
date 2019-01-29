/*
 * Copyright 2019 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.output.json;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class CDOMSerializer
{

	public static void addStandardItems(JsonObject jsonObject, PObject cdo,
		JsonSerializationContext context)
	{
		addIdentifier(jsonObject, cdo);
		jsonObject.addProperty("displayName", cdo.getDisplayName());
		SerializerUtilities.writeFieldIfPresent(jsonObject, "localScopeName",
			cdo.getLocalScopeName(), context);
		jsonObject.addProperty("isInternal", cdo.isInternal());
		jsonObject.addProperty("nameIsPI", cdo.isNamePI());
		jsonObject.addProperty("descIsPI", cdo.getSafe(ObjectKey.DESC_PI));
		jsonObject.addProperty("outputName", cdo.getOutputName());
		Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
		if (campaign != null)
		{
			jsonObject.addProperty("sourceCampaign", campaign.getKeyName());
		}
		jsonObject.add("sourceURI", context.serialize(cdo.getSourceURI()));
		SerializerUtilities.writeStringFieldIfNotBlank(jsonObject, "sourceWeb",
			cdo.get(StringKey.SOURCE_WEB));
		SerializerUtilities.writeStringFieldIfNotBlank(jsonObject,
			"sourceShort", cdo.get(StringKey.SOURCE_SHORT));
		SerializerUtilities.writeStringFieldIfNotBlank(jsonObject, "sourceLong",
			cdo.get(StringKey.SOURCE_LONG));
		SerializerUtilities.writeStringFieldIfNotBlank(jsonObject, "sourcePage",
			cdo.get(StringKey.SOURCE_PAGE));
		SerializerUtilities.writeStringFieldIfNotBlank(jsonObject, "sourceLink",
			cdo.get(StringKey.SOURCE_LINK));
		SerializerUtilities.writeStringFieldIfNotBlank(jsonObject, "sourceWeb",
			cdo.get(StringKey.SOURCE_WEB));
		SerializerUtilities.writeObjectFieldIfNotNull(jsonObject, "sourceDate",
			cdo.get(ObjectKey.SOURCE_DATE), context);

		SerializerUtilities.writeStringFieldIfNotBlank(jsonObject, "sortKey",
			cdo.get(StringKey.SORT_KEY));

		//END to-do
		jsonObject.add("type", context.serialize(cdo.getTrueTypeList(true),
			SerializerUtilities.LISTTYPE_TYPE));

		writeFactSets(jsonObject, cdo);
		writeFacts(jsonObject, cdo);
	}

	public static void addIdentifier(JsonObject jsonObject, PObject cdo)
	{
		jsonObject.addProperty("format",
			StringPClassUtil.getStringFor(cdo.getClass()));
		jsonObject.addProperty("key", cdo.getKeyName());
	}

	private static void writeFacts(JsonObject jsonObject, PObject cdo)
	{
		Set<FactKey<?>> keys = cdo.getFactKeys();
		if (keys.isEmpty())
		{
			return;
		}
		JsonArray factBlock = new JsonArray();
		for (FactKey<?> factKey : keys)
		{
			processFact(factBlock, cdo, factKey);
		}
		jsonObject.add("fact", factBlock);
	}

	private static <T> void processFact(JsonArray factBlock, PObject cdo,
		FactKey<T> factKey)
	{
		JsonObject factDetail = new JsonObject();
		FormatManager<T> formatManager = factKey.getFormatManager();
		Indirect<T> fact = cdo.get(factKey);
		factDetail.addProperty("factName", factKey.toString());
		factDetail.addProperty("format", formatManager.getIdentifierType());
		factDetail.addProperty("value", formatManager.unconvert(fact.get()));
		factBlock.add(factDetail);
	}

	private static void writeFactSets(JsonObject jsonObject, PObject cdo)
	{
		Set<FactSetKey<?>> keys = cdo.getFactSetKeys();
		if (keys.isEmpty())
		{
			return;
		}
		JsonArray factSetBlock = new JsonArray();
		for (FactSetKey<?> factSetKey : keys)
		{
			processFactSet(factSetBlock, cdo, factSetKey);
		}
		jsonObject.add("factSet", factSetBlock);
	}

	private static <T> void processFactSet(JsonArray factSetBlock, PObject cdo,
		FactSetKey<T> factSetKey)
	{
		JsonObject factSetDetail = new JsonObject();
		FormatManager<T> formatManager = factSetKey.getFormatManager();
		List<Indirect<T>> factSet = cdo.getSetFor(factSetKey);
		factSetDetail.addProperty("factSetName", factSetKey.toString());
		factSetDetail.addProperty("format", formatManager.getIdentifierType());
		JsonArray jsonFactSetInfo = new JsonArray();
		for (Indirect<T> indirect : factSet)
		{
			jsonFactSetInfo.add(formatManager.unconvert(indirect.get()));
		}
		factSetDetail.add("value", jsonFactSetInfo);
		factSetBlock.add(factSetDetail);
	}

	public static void writePreReq(JsonObject jsonObject, CDOMObject cdo)
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
		jsonObject.addProperty("prerequisites", sb.toString());
	}

	public static void writeDescription(JsonObject jsonObject, PObject cdo)
	{
		PlayerCharacter pc = SerializerContext.pcContext.get();
		jsonObject.addProperty("description", DescriptionFormatting
			.piWrapDesc(cdo, pc.getDescription(cdo), false));
	}

	public static void writeList(JsonObject jsonObject, CDOMObject cdo,
		String name, ListKey<?> key, JsonSerializationContext context)
	{
		SerializerUtilities.writeList(jsonObject, name, cdo.getListFor(key),
			context);
	}

}
