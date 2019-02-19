package domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
public class Procession extends DomainEntity {

	/* Attributes */
	private String ticker;
	private String title;
	private String description;
	private Date organisedMoment;
	private boolean isDraft;
	// Float es un tipo ya predefinido en java, por lo que por ahora Float pasa
	// a ser Platform.
	private Collection<Platform> platforms;
	private Brotherhood brotherhood;

	/* Getters&Setters */

	@NotBlank
	//@Pattern(regexp = "")
	// TODO Pattern
	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	@NotBlank
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@NotBlank
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getOrganisedMoment() {
		return organisedMoment;
	}

	public void setOrganisedMoment(Date organisedMoment) {
		this.organisedMoment = organisedMoment;
	}

	public boolean getIsDraft() {
		return isDraft;
	}

	public void setIsDraft(boolean isDraft) {
		this.isDraft = isDraft;
	}

	@Valid
	@ManyToMany
	public Collection<Platform> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(Collection<Platform> platforms) {
		this.platforms = platforms;
	}

	@Valid
	@OneToOne(optional = false)
	public Brotherhood getBrotherhood() {
		return brotherhood;
	}

	public void setBrotherhood(Brotherhood brotherhood) {
		this.brotherhood = brotherhood;
	}

}