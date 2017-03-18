package my.spike.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ValidationPreFilter extends ZuulFilter {

    private RestTemplate restTemplate;

    public ValidationPreFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        final String token = RequestContext.getCurrentContext().getRequest().getHeader("token");
        final String response = restTemplate
                .postForObject("http://localhost:8080/auth/validate?token={0}", null, String.class, token);
        if(!"ok".equals(response)) {
            RequestContext.getCurrentContext().setResponseStatusCode(401);
            RequestContext.getCurrentContext().setSendZuulResponse(false);
        }
        RequestContext.getCurrentContext().addZuulRequestHeader("userid", "admin");
        return null;
    }
}
