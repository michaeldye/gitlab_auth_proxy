# gitlab_auth_proxy

A web proxy that authenticates with GitLab using Basic Authentication before proxying a request. Given success, the proxy will proxy the original request to a specified endpoint. If authentication fails, the original request is not proxied.

The original intent of this application is to frontend authentication to PyPI server in an environment with a GitLab instance all of whose users should have permission to use the PyPI server.

## Production Execution

    (export GITLAB_URL="https://repo.hovitos.engineering/api/v3/session"; export PROXY_URL="https://pypi.hovitos.engineering/"; export PORT=9001; java -jar target/gitlab_auth_proxy-0.1.0-SNAPSHOT-standalone.jar )

Once the daemon is started, an admin should deploy an SSL reverse proxy that proxies requests to this instance and remove authentication from the PyPI server. **Never** deploy this server without SSL transport: HTTP Basic Authentication sends decodable credentials in an HTTP header.

## Development Usage

    (export GITLAB_URL="https://repo.hovitos.engineering/api/v3/session"; export PROXY_URL="https://pypi.hovitos.engineering/"; export PORT=9001; lein ring server-headless)

In this mode, code changes will cause runtime reloads.

## TODO:

- Cache session so authentication step needn't be performed on each request
- Add proxy headers
