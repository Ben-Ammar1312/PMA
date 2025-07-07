# Codex Agent for PMA (Spring Boot)

## Setup
```bash
# Use project-local Maven cache
export MAVEN_USER_HOME="$PWD/.m2"

# Download ALL project dependencies once
# (needs Internet only the first run)
./mvnw -q --batch-mode --no-transfer-progress \
       -Dmaven.repo.local=.m2 dependency:go-offline
```

## Testing
```bash
# Run unit + integration tests completely OFFLINE
./mvnw -q verify -Dmaven.repo.local=.m2 -o
```