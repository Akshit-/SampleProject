package common.messages;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import common.messages.KVMessage.StatusType;

/**
 * 
 */
public class JSONSerializer {

	/**
	 * 
	 * @param
	 * @return TextMessage
	 */
	public static TextMessage marshal(String key, String value,
			StatusType status) {
		KVMessageImpl msg = new KVMessageImpl(key, value, status);
		JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
		JsonObject object = objectBuilder.add("key", msg.getKey())
				.add("value", msg.getValue())
				.add("status", msg.getStatus().ordinal()).build();
		return new TextMessage(object.toString());
	}

	/**
	 * 
	 * @param
	 * @return KVMessageImpl
	 */
	public static KVMessageImpl unMarshal(TextMessage txtMsg) {
		String strMsg = txtMsg.getMsg();
		JsonObject jsonObject = Json.createReader(new StringReader(strMsg))
				.readObject();
		return new KVMessageImpl(jsonObject.getString("key"),
				jsonObject.getString("value"),
				KVMessageImpl.getStatusType(jsonObject.getInt("status")));
	}

}
