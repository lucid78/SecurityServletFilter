# SecurityServletFilter

This is the servlet filter using spring security filter.
This filter is used after the FilterSecurityInterceptor which is included in the spring security framework by default.
At the time this filter is called, the user already knows that authentication is complete and is a valid user in the system.

Filter rules can be specified in URL units or parameter units.

## dependent
  For Url based Route, we used [Routd](https://github.com/lantunes/routd)
  
  [Spring Security](https://github.com/spring-projects/spring-security)
  
  
## using
  - add jar or source
  - add java config and extend WebSecurityConfigurerAdapter
  - add filter rule

## example

```java

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(new SecurityFilter(), FilterSecurityInterceptor.class);

        // Create New Filter Rule
        final FilterRule filterRule = new FilterRule();

        // URL Filter
        filterRule.addRule("/client/:name",             URLFilter, CHARACTER);
        filterRule.addRule("/customer/:id<[0-9]+>",     URLFilter, NUMBER);
        filterRule.addRule("/admin/*",                  URLFilter, CHARNUM);
        filterRule.addRule("/manager/:name<[a-z]+>",    URLFilter, BLACKLIST, "-;'\",.");

        // PARAM Filter
        filterRule.addRule("/login",                    PARAMFilter, "id",      CHARACTER);
        filterRule.addRule("/login",                    PARAMFilter, "pwd",     NUMBER);
        filterRule.addRule("/board/*/view/",            PARAMFilter, "id",      NUMBER);
        filterRule.addRule("/member/*/*/*",             PARAMFilter, "name",    BLACKLIST, "-;'\",.!");

        http.addFilterAfter(filterRule, SecurityFilter.class);
    }
}

```

## Result

#### URL Filter

http://localhost:8080/admin/auth?cid=9a9b!8c&role=hh$g6*65!2
-> cid: 9a9b8c, role: hhg6652

#### Parameter Filter

http://localhost:8080/member/a/b/c?name=abc123-..!@#;llazcv-
-> name: abc123


