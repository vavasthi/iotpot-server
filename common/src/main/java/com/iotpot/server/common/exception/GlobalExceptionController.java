/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.PersistenceException;

/*o/.
  This class is responsible of handling all exceptions, like giving proper response back
  to client, whether to log exception or not
*/
@ControllerAdvice
public class GlobalExceptionController extends ResponseEntityExceptionHandler {

  private static final Logger logger = Logger.getLogger(GlobalExceptionController.class);


  /**
   * Handle exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = org.springframework.security.access.AccessDeniedException.class)
  @ResponseBody
  public ResponseEntity<Object> handleAccessDeniedException(
          WebRequest request,
          Exception hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.FORBIDDEN;
    String message = hbe.getMessage();
    e = new ExceptionResponseBuilder()
            .setCode(status.value())
            .setMessage(message)
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  /**
   * Handle exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = BlackListedDueToRepeatedAuthFailureException.class)
  @ResponseBody
  public ResponseEntity<Object> handleBlackListedDueToRepeatedAuthFailureException(
          WebRequest request,
          BlackListedDueToRepeatedAuthFailureException hbe) {
    ExceptionResponse e;
    logger.error(hbe.getMessage(), null);
    HttpStatus status = HttpStatus.FORBIDDEN;
    String message = hbe.getMessage();
    e = new ExceptionResponseBuilder()
            .setCode(status.value())
            .setMessage(message)
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    return handleExceptionInternal(hbe, e, headers, status, request);
  }



  /**
   * Handle exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = JsonMappingException.class)
  @ResponseBody
  public ResponseEntity<Object> handleJsonMappingException(WebRequest request, Exception hbe) {
    ExceptionResponse e;
    logger.error(hbe.getMessage(), hbe);
    HttpStatus status = HttpStatus.FAILED_DEPENDENCY;
    String message = hbe.getMessage();
    e = new ExceptionResponseBuilder()
            .setCode(status.value())
            .setMessage(message)
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    return handleExceptionInternal(hbe, e, headers, status, request);
  }



  /**
   * Handle exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = NullPointerException.class)
  @ResponseBody
  public ResponseEntity<Object> handleNullPointerException(WebRequest request, Exception hbe) {
    ExceptionResponse e;
    logger.error(hbe.getMessage(), hbe);
    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    String message = hbe.getMessage();
    e = new ExceptionResponseBuilder()
            .setCode(status.value())
            .setMessage(message)
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    return handleExceptionInternal(hbe, e, headers, status, request);
  }



  /**
   * Handle exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = Exception.class)
  @ResponseBody
  public ResponseEntity<Object> handleException(WebRequest request, Exception hbe) {
    ExceptionResponse e;
    logger.error(hbe.getMessage(), hbe);
    HttpStatus status = org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

    String message = hbe.getMessage();
    e = new ExceptionResponseBuilder()
        .setCode(status.value())
        .setMessage(message)
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  /**
   * Handle hubble base exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = IoTPotBaseException.class)
  @ResponseBody
  public ResponseEntity<Object> handleHubbleBaseException(WebRequest request, IoTPotBaseException hbe) {
    ExceptionResponse e;
    if (hbe.shouldLog()) {

      logger.error(hbe.getMessage(), hbe);
    }
    else {
      logger.error(hbe.getMessage());
    }
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String message = hbe.getMessage();
    e = new ExceptionResponseBuilder()
        .setCode(status.value())
        .setMessage(message)
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
        .setStatus(hbe.getErrorCode())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    return handleExceptionInternal(hbe, e, headers, status, request);
  }


  /**
   * Handle infrastructure platform exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = InfrastructurePlatformException.class)
  @ResponseBody
  public ResponseEntity<Object> handleInfrastructurePlatformException(
          WebRequest request,
          Exception hbe) {
    ExceptionResponse e;
    logger.error(hbe.getMessage(), hbe);
    HttpStatus status = HttpStatus.FAILED_DEPENDENCY;
    e = new ExceptionResponseBuilder()
            .setCode(status.value())
            .setMessage(hbe.getMessage())
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }


  /**
   * Handle transaction system exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = TransactionSystemException.class)
  @ResponseBody
  public ResponseEntity<Object> handleTransactionSystemException(
          WebRequest request,
          Exception hbe) {
    ExceptionResponse e;
    logger.error(hbe.getMessage(), hbe);
    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    e = new ExceptionResponseBuilder()
            .setCode(status.value())
            .setMessage(hbe.getMessage())
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }


  /**
   * Handle patching exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = PatchingException.class)
  @ResponseBody
  public ResponseEntity<Object> handlePatchingException(WebRequest request, Exception hbe) {
    ExceptionResponse e;
    logger.error(hbe.getMessage(), hbe);
    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    e = new ExceptionResponseBuilder()
        .setCode(status.value())
            .setMessage(hbe.getMessage())
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }


  /**
   * Handle persistence exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = PersistenceException.class)
  @ResponseBody
  public ResponseEntity<Object> handlePersistenceException(WebRequest request, Exception hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    logger.error(hbe.getMessage(), hbe);
    e = new ExceptionResponseBuilder()
        .setCode(status.value())
            .setMessage(hbe.getMessage())
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }


  /**
   * Handle authentication exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = AuthenticationException.class)
  @ResponseBody
  public ResponseEntity<Object> handleAuthenticationException(WebRequest request, Exception hbe) {
    ExceptionResponse e;
    HttpStatus status = org.springframework.http.HttpStatus.UNAUTHORIZED;
    logger.error(hbe.getMessage());
    e = new ExceptionResponseBuilder()
            .setCode(status.value())
            .setMessage(hbe.getMessage())
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  /**
   * Handle not found exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = NotFoundException.class)
  @ResponseBody
  public ResponseEntity<Object> handleNotFoundException(WebRequest request, NotFoundException hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.NOT_FOUND;
    logger.error(hbe.getMessage());
    e = new ExceptionResponseBuilder()
        .setCode(hbe.getErrorCode())
            .setMessage(hbe.getMessage())
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  /**
   * Handle entity not found exception response entity.
   *
   * @param request the request
   * @param hbe    the enfe
   * @return the response entity
   */
  @ExceptionHandler(value = EntityNotFoundException.class)
  @ResponseBody
  public ResponseEntity<Object> handleEntityNotFoundException(WebRequest request, EntityNotFoundException hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.NOT_FOUND;
    logger.error(hbe.getMessage());
    e = new ExceptionResponseBuilder()
            .setCode(hbe.getErrorCode())
            .setMessage(hbe.getMessage())
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  /**
   * Handle unprocessable exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = UnprocessableException.class)
  @ResponseBody
  public ResponseEntity<Object> handleUnprocessableException(WebRequest request, UnprocessableException hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    logger.error(hbe.getMessage(), hbe);
    e = new ExceptionResponseBuilder()
            .setCode(hbe.getErrorCode())
            .setMessage(hbe.getMessage())
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }
  /**
   * Handle event retrieval exception.
   *
   * @param request the request
   * @param ee     the ee
   * @return the response entity
   */
  @ExceptionHandler(value = EventRetrievalException.class)
  @ResponseBody
  public ResponseEntity<Object> handleEventRetrievalException(WebRequest request, EventRetrievalException ee) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    logger.error(ee.getMessage(), ee);
    e = new ExceptionResponseBuilder()
            .setCode(ee.getErrorCode())
            .setMessage(ee.getMessage())
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(ee, e, headers, status, request);
  }

  /**
   * Handle camera service exceptions response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = CameraServiceException.class)
  @ResponseBody
  public ResponseEntity<Object> handleCameraServiceExceptions(WebRequest request, Exception hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.BAD_REQUEST;
    logger.error(hbe.getMessage(), hbe);
    e = new ExceptionResponseBuilder()
        .setCode(status.value())
            .setMessage(hbe.getMessage())
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
     headers.setContentType(MediaType.TEXT_HTML);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }


  /**
   * Handle bad request exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = BadRequestException.class)
  @ResponseBody
  public ResponseEntity<Object> handleBadRequestException(WebRequest request, BadRequestException hbe) {
    ExceptionResponse e;
    HttpStatus status = org.springframework.http.HttpStatus.BAD_REQUEST;
    logger.error(hbe.getMessage());
    e = new ExceptionResponseBuilder()
        .setCode(hbe.getCode())
            .setMessage(hbe.getMessage())
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, hbe.getCode()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  /**
   * Handle bad request exception response entity.
   *
   * @param request the request
   * @param bce     the bce
   * @return the response entity
   */
  @ExceptionHandler(value = BadCredentialsException.class)
  @ResponseBody
  public ResponseEntity<Object> handleBadCredentialsException(WebRequest request, Exception bce) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    logger.error(bce.getMessage());
    e = new ExceptionResponseBuilder()
            .setCode(status.value())
            .setMessage(bce.getMessage())
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(bce, e, headers, status, request);
  }

  /**
   * Handle access denied request exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = AccessDeniedException.class)
  @ResponseBody
  public ResponseEntity<Object> handleAccessDeniedRequestException(WebRequest request, AccessDeniedException hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    logger.error(hbe.getMessage());
    e = new ExceptionResponseBuilder()
        .setCode(hbe.getCode())
            .setMessage(hbe.getMessage())
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, hbe.getCode()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  /**
   * Handle authorization exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = AuthorizationException.class)
  @ResponseBody
  public ResponseEntity<Object> handleAuthorizationException(WebRequest request, Exception hbe) {
    ExceptionResponse e;
    HttpStatus status = org.springframework.http.HttpStatus.FORBIDDEN;
    logger.error(hbe.getMessage());
    e = new ExceptionResponseBuilder()
        .setCode(status.value())
            .setMessage(hbe.getMessage())
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  /**
   * Handle generic jdbc exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = GenericJDBCException.class)
  @ResponseBody
  public ResponseEntity<Object> handleGenericJDBCException(WebRequest request, Exception hbe) {
    ExceptionResponse e;
    HttpStatus status = org.springframework.http.HttpStatus.BAD_REQUEST;
    logger.error(hbe.getMessage(), hbe);
    String message = hbe.getMessage();
    e = new ExceptionResponseBuilder()
        .setCode(status.value())
        .setMessage(message)
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  /**
   * Handle resource not found exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = ResourceNotFoundException.class)
  @ResponseBody
  public ResponseEntity<Object> handleResourceNotFoundException(WebRequest request, Exception hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.NOT_FOUND;
    logger.error(hbe.getMessage());
    String message = hbe.getMessage();
    e = new ExceptionResponseBuilder()
        .setCode(status.value())
        .setMessage(message)
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  /**
   * Handle failed dependency exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = FailedDependencyException.class)
  @ResponseBody
  public ResponseEntity<Object> handleFailedDependencyException(WebRequest request, FailedDependencyException hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.FAILED_DEPENDENCY;

    String message = hbe.getMessage() + " " + getRootCause(hbe);
    logger.error(message);
    e = new ExceptionResponseBuilder()
        .setCode(hbe.getErrorCode())
        .setMessage(message)
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, hbe.getErrorCode()))
        .setStatus(hbe.getErrorCode())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  /**
   * Handle message exception response entity.
   *
   * @param request the request
   * @param hbe     the mse
   * @return the response entity
   */
  @ExceptionHandler(value = FailDependencyMessageException.class)
  @ResponseBody
  public ResponseEntity<Object> handleMessageException(WebRequest request, FailDependencyMessageException hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.FAILED_DEPENDENCY;
    logger.error(hbe.getMessage(), hbe);
    String message = hbe.getMessage();
    e = new ExceptionResponseBuilder()
            .setCode(hbe.getErrorCode())
            .setMessage(message)
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, hbe.getErrorCode()))
            .setStatus(hbe.getErrorCode())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe, e, headers, status, request);
  }


  /**
   * Handle recurly hubble exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = BusinessInterfaceException.class)
  @ResponseBody
  public ResponseEntity<Object> handleRecurlyHubbleException(WebRequest request, BusinessInterfaceException hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.FAILED_DEPENDENCY;
    logger.error(hbe.getMessage(), hbe);
    e = new ExceptionResponseBuilder()
        .setCode(hbe.getErrorCode())
            .setMessage(hbe.getMessage())
        .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
        .setStatus(status.value())
        .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe,e, headers,status, request);
  }

  /**
   * Handle internal error with data exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value=InternalErrorWithDataException.class)
  @ResponseBody
  public ResponseEntity<Object> handleInternalErrorWithDataException(
          WebRequest request,
          InternalErrorWithDataException hbe) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.FAILED_DEPENDENCY;
    logger.error(hbe.getMessage(), hbe);
    e = new ExceptionResponseBuilder()
            .setCode(hbe.getErrorCode())
            .setMessage(hbe.getMessage())
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, hbe.getErrorCode()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(hbe,e, headers,status, request);
  }

  @ExceptionHandler(value= ObjectOptimisticLockingFailureException.class)
  @ResponseBody
  public ResponseEntity<Object> handleOptimisticLockingException(
          WebRequest request,
          ObjectOptimisticLockingFailureException  ex) {
    ExceptionResponse e;
    HttpStatus status = HttpStatus.FAILED_DEPENDENCY;
    logger.error(ex.getMessage(), ex);
    e = new ExceptionResponseBuilder()
            .setCode(status.value())
            .setMessage(ex.getMessage())
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(ex,e, headers,status, request);
  }

  /**
   * Handle exception response entity.
   *
   * @param request the request
   * @param hbe     the hbe
   * @return the response entity
   */
  @ExceptionHandler(value = TooManyRequestsException.class)
  @ResponseBody
  public ResponseEntity<Object> handleTooManyRequestsException(
          WebRequest request,
          TooManyRequestsException hbe) {
    ExceptionResponse e;
    logger.error(hbe.getMessage(), null);
    HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
    String message = hbe.getMessage();
    e = new ExceptionResponseBuilder()
            .setCode(status.value())
            .setMessage(message)
            .setMoreInfo(String.format(IoTPotConstants.EXCEPTION_URL, status.value()))
            .setStatus(status.value())
            .createExceptionResponse();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    return handleExceptionInternal(hbe, e, headers, status, request);
  }

  private String getRootCause(Throwable ex) {
    if (ex == null) {
      return StringUtils.EMPTY;
    }

    if (ex.getCause() == null) {
      return ex.getMessage();
    }

    return getRootCause(ex.getCause());
  }
}
