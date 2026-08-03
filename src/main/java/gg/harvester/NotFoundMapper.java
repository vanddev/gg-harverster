package gg.harvester;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class NotFoundMapper implements ResponseExceptionMapper<RuntimeException> {
  @Override
  public RuntimeException toThrowable(Response response) {
      if (response.getStatus() == 404) {
        return new NotFoundException();
      }
      return null;
  }

  @Override
  public int getPriority() {
    return ResponseExceptionMapper.super.getPriority();
  }

  @Override
  public boolean handles(int status, MultivaluedMap headers) {
    return status == 404;
  }
}
