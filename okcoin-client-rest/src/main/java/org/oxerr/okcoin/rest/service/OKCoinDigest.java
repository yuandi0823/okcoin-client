package org.oxerr.okcoin.rest.service;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.FormParam;

import org.knowm.xchange.utils.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.mazi.rescu.Params;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

/**
 * {@link ParamsDigest} implementation.
 */
public class OKCoinDigest implements ParamsDigest {

	private final Logger log = LoggerFactory.getLogger(OKCoinDigest.class);

	private final String secretKey;
	private final MessageDigest md;

	public OKCoinDigest(String secretKey) {
		this.secretKey = secretKey;
		try {
			this.md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String digestParams(RestInvocation restInvocation) {
		return digestParams(restInvocation.getParamsMap().get(FormParam.class).asHttpHeaders());
	}

	public String digestParams(Map<String, String> parameters) {
		Params params = Params.of();

		new TreeMap<>(parameters)
				.entrySet()
				.stream()
				.filter(e -> !e.getKey().equals("sign"))
				.forEach(e -> params.add(e.getKey(), e.getValue()));

		String payload = format("%s&secret_key=%s", params.toString(), secretKey);
		log.trace("payload: {}", payload);

		return DigestUtils.bytesToHex(md.digest(payload.getBytes(UTF_8)));
	}

}
