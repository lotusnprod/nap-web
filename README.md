Soon we will explain.

Code is licensed under AGPL 3.0 except for SnorQL by Ammar Ammar (ammar257ammar@gmail.com) and collaborators that is using GPL v3.0

## Developers 

When working locally set the following environment variables

If using podman/docker:

```shell
# For production deployment
./run

# For development with Docker/Podman
./run-dev  # This starts only the nap-sparql service (accessible at http://localhost:3030) without nap-web

# If you want to run from IntelliJ
./run-dev  # This starts only the nap-sparql service
# No need to stop nap-web as it's not started by run-dev
```

Note: You may need to make the scripts executable with `chmod +x run-dev` if they aren't already.
