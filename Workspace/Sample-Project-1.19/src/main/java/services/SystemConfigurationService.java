package services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.SystemConfigurationRepository;
import domain.Actor;
import domain.SystemConfiguration;

@Service
@Transactional
public class SystemConfigurationService {

	// Managed Repository

	@Autowired
	private SystemConfigurationRepository systemConfigurationRepository;

	// Supporting Services

	@Autowired
	private ActorService actorService;

	// Simple CRUD methods

	/* Find one by ID */
	public SystemConfiguration findOne(final int systemConfigurationId) {
		SystemConfiguration res;

		res = this.systemConfigurationRepository.findOne(systemConfigurationId);

		return res;

	}

	/* Find all system configurations */
	public Collection<SystemConfiguration> findAll() {
		Collection<SystemConfiguration> result;

		result = this.systemConfigurationRepository.findAll();

		return result;
	}

	/* Create a system configuration */
	public SystemConfiguration create() {
		Actor principal;

		principal = this.actorService.findByPrincipal();
		Assert.isTrue(
				this.actorService.checkAuthority(principal, "ADMINISTRATOR"),
				"not.allowed");

		Map<String, String> wellMap = new HashMap<>();
		wellMap.put("Español",
				"¡Bienvenidos a Acme Madrugá! Tu sitio para organizar procesiones.");
		wellMap.put("English",
				"Welcome to Acme Madrugá, the site to organise your processions.");

		SystemConfiguration systemConfiguration = new SystemConfiguration();
		systemConfiguration.setSystemName("Acme-Madrugá");
		systemConfiguration.setWelcomeMessage(wellMap);
		systemConfiguration
				.setBanner("https://image.ibb.co/iuaDgV/Untitled.png");
		systemConfiguration.setCountryCode("+034");
		systemConfiguration.setTimeResultsCached(1);
		systemConfiguration.setMaxResults(10);
		systemConfiguration
				.setSpamWords("sex,viagra,cialis,one million,you've been selected,nigeria,sexo,un millon,un millón,ha sido seleccionado");
		systemConfiguration
				.setPossitiveWords("good,fantastic,excellent,great,amazing,terrific,beautiful,bueno,fantastico,fantástico,excelente,genial,"
						+ "increíble,increible,asombroso,bonito");
		systemConfiguration
				.setNegativeWords("not,bad,horrible,average,disaster,no,malo,mediocre,desastre,desastroso");
		return systemConfiguration;
	}

	/* Saving a system configuration */
	public SystemConfiguration save(SystemConfiguration systemConfiguration) {
		Assert.notNull(systemConfiguration, "null.system.configuration");
		Actor principal;

		principal = this.actorService.findByPrincipal();
		Assert.isTrue(
				this.actorService.checkAuthority(principal, "ADMINISTRATOR"),
				"not.allowed");

		systemConfiguration.setId(this.systemConfigurationRepository.findAll()
				.get(0).getId());

		SystemConfiguration result;
		result = this.systemConfigurationRepository.save(systemConfiguration);

		return result;
	}

	/* Delete system configuration */
	public void delete(SystemConfiguration systemConfiguration) {
		Assert.notNull(systemConfiguration, "null.system.configuration");

		Actor principal;
		principal = this.actorService.findByPrincipal();
		Assert.isTrue(
				this.actorService.checkAuthority(principal, "ADMINISTRATOR"),
				"not.allowed");

		this.systemConfigurationRepository.delete(systemConfiguration);
	}

	// Other business methods

	/* Find system configuration */
	SystemConfiguration findMySystemConfiguration() {
		final SystemConfiguration result;

		result = this.systemConfigurationRepository.findSystemConf();

		return result;
	}

	/* Find banner */
	public String findMyBanner() {

		String result;

		result = this.findMySystemConfiguration().getBanner();

		return result;
	}

	/* Find welcome message */
	public Map<String, String> findWelcomeMessage() {
		final Map<String, String> result;

		result = this.findMySystemConfiguration().getWelcomeMessage();

		return result;
	}

}