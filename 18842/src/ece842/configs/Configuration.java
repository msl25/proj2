package ece842.configs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import ece842.fileserver.Fileserver;

public class Configuration {
	private static final String PEERS = "Peers";
	private static final String NAME = "Name";
	private static final String IP = "IP";
	private static final String PORT = "Port";
	private static final String SEND_RULES = "sendRules";
	private static final String RECEIVE_RULES = "receiveRules";

	private String conf_url;
	private Map<String, Addr> peers = new HashMap<String, Addr>();
	private List<Rule> sendRules = new ArrayList<Rule>();
	private List<Rule> recvRules = new ArrayList<Rule>();
	private String clockType = null;

	public Configuration (String conf_url) throws IOException{

		this.conf_url = conf_url;

		this.updateClockType();
		this.updatePeers();
		this.updateRules();
	}

	private void updateClockType () throws IOException {
		URL clockTypeUrl = new URL(this.conf_url.concat("clockType.txt"));
		URLConnection con = clockTypeUrl.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		String clockType = IOUtils.toString(in, encoding);
		if (clockType.startsWith("logical")) {
			this.clockType = "logical";
		} else if (clockType.startsWith("vector")) {
			this.clockType = "Vector";
		} else {
			this.clockType = "logical";
		}
	}

	private void updatePeers() throws IOException {
		Yaml yaml = new Yaml();
		String filecontents = Fileserver.getFile(this.conf_url.concat("peers.txt"));
		InputStream in = new ByteArrayInputStream(filecontents.getBytes());
		@SuppressWarnings("unchecked")
		Map<String, List<Map<String, Object>>> yamlData = (Map<String, List<Map<String, Object>>>) yaml
		.load(in);

		List<Map<String, Object>> connections = yamlData.get(PEERS);
		for (Map<String, Object> connection : connections) {
			Addr connectionParameters = new Addr(
					connection.get(IP), connection.get(PORT));
			this.peers.put((String) connection.get(NAME),
					connectionParameters);
		}
	}

	public void updateRules() throws IOException {
		this.sendRules.clear();
		this.recvRules.clear();
		Yaml yaml = new Yaml();
		String filecontents = Fileserver.getFile(this.conf_url.concat("rules.txt"));
		InputStream in = new ByteArrayInputStream(filecontents.getBytes());
		@SuppressWarnings("unchecked")
		Map<String, List<Map<String, Object>>> yamlData = (Map<String, List<Map<String, Object>>>) yaml
		.load(in);

		// sendRules
		List<Map<String, Object>> sendRules = yamlData.get(SEND_RULES);
		if (sendRules != null) {
			for (Map<String, Object> sendRule : sendRules) {
				this.sendRules.add(new Rule(sendRule));
			}
		}

		// receiveRules
		List<Map<String, Object>> receiveRules = yamlData.get(RECEIVE_RULES);
		if (receiveRules != null) {
			for (Map<String, Object> receiveRule : receiveRules) {
				this.recvRules.add(new Rule(receiveRule));
			}
		}

		// defaultRule
		Map<String, Object> defaultParameters = new HashMap<String, Object>();
		defaultParameters.put("action","default");
		this.sendRules.add(new Rule(defaultParameters));
		this.recvRules.add(new Rule(defaultParameters));
	}

	public Map<String, Addr> getPeers() {
		return this.peers;
	}

	public List<Rule> getSendRules() {
		return this.sendRules;
	}

	public List<Rule> getReceiveRules() {
		return this.recvRules;
	}

	public String getClockType() {
		return this.clockType;
	}
}