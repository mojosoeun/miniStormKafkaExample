#!/bin/bash

set -e

host="$1"
shift
cmd="$@"

until /usr/bin/start-supervisor.sh; do
  >&2 echo "Apache Storm's Nimbus is unavailable - sleeping"
  sleep 1
done

>&2 echo "Apache Storm's Nimbus is up - executing command"
exec $cmd
