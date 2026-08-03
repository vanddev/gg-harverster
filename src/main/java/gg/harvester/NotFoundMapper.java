package gg.harvester;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class NotFoundMapper implements ResponseExceptionMapper {
  @Override
  public Throwable toThrowable(Response response) {
    return null;
  }

  @Override
  public int getPriority() {
    return ResponseExceptionMapper.super.getPriority();
  }

  @Override
  public boolean handles(int status, MultivaluedMap headers) {
    return ResponseExceptionMapper.super.handles(status, headers);
  }
}
