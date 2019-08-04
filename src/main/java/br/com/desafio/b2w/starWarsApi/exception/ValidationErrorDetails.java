package br.com.desafio.b2w.starWarsApi.exception;

/**
 * 
 * @author Leonardo Rocha
 *
 */
public class ValidationErrorDetails extends ErrorDetails {
	
	private String field;
	private String fieldMessage;

	public static final class Builder {
		
		protected String title;
		protected String timestamp;
		private String detail;
		private String developerMessage;
		private String field;
		private String fieldMessage;
		private int status;

		private Builder() {}

		public static Builder newBuilder() {
			return new Builder();
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder status(int status) {
			this.status = status;
			return this;
		}

		public Builder detail(String detail) {
			this.detail = detail;
			return this;
		}

		public Builder timestamp(String timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder developerMessage(String developerMessage) {
			this.developerMessage = developerMessage;
			return this;
		}

		public Builder field(String field) {
			this.field = field;
			return this;
		}

		public Builder fieldMessage(String fieldMessage) {
			this.fieldMessage = fieldMessage;
			return this;
		}

		public ValidationErrorDetails build() {
			ValidationErrorDetails veDetail = new ValidationErrorDetails();
			veDetail.setDeveloperMessage(developerMessage);
			veDetail.setDetail(detail);
			veDetail.setStatus(status);
			veDetail.fieldMessage = fieldMessage;
			veDetail.field = field;
			
			return veDetail;
		}
	}

	public String getField() {
		return field;
	}

	public String getFieldMessage() {
		return fieldMessage;
	}
}
