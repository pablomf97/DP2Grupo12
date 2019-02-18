package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class MessageBox extends DomainEntity {

	/* Attributes */

	private String name;
	private boolean isPredefined;
	private Collection<MessageBox> parentMessageBoxes;
	private Collection<Message> messages;
	private Actor actor;

	/* Getters&Setters */

	@NotBlank
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPredefined() {
		return isPredefined;
	}

	public void setPredefined(boolean isPredefined) {
		this.isPredefined = isPredefined;
	}

	@ElementCollection
	@OneToMany
	// OneToOne?
	public Collection<MessageBox> getParentMessageBoxes() {
		return parentMessageBoxes;
	}

	public void setParentMessageBoxes(Collection<MessageBox> parentMessageBoxes) {
		this.parentMessageBoxes = parentMessageBoxes;
	}

	@ManyToMany
	public Collection<Message> getMessages() {
		return messages;
	}

	public void setMessages(Collection<Message> messages) {
		this.messages = messages;
	}

	@Valid
	@NotNull
	@OneToOne(optional = false)
	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

}
