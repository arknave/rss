## RSS

Incredibly simple RSS "reader". Given a list of RSS feeds in `feeds.txt`, this program pulls the latest entries from each feed and aggregates it into a single web page. Built using some of Twitter's libraries.

```
usage: ./sbt run path/to/feeds.txt
```

The HTML is then routed to standard out.

TODO:
- Clean up output
- Do better error handling
- Put this up behind a server
- Validate descriptions, prevent injections
