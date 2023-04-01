# App Json Schemas
## Schema v3
`schemaVersion`: The schema version\
`name`: The name of the application\
`authors`: The authors of the application, provided in an array\
`contributors`: Additional contributors to the application\
`description`: The description of the application to show up in the Application Market\
`version`: The application version, preferably following the SemVer 2.0 specification\
`screenshots`: URLs to a location online starting with `https://` or `http://` for remote images or an identifier/resource location for local images\
`support`: See [SupportSchema.md](SupportSchema.md#schema-v2) v2

## Schema v2
`schemaVersion`: The schema version\
`name`: The name of the application\
`authors`: The authors of the application, provided in an array\
`author`: The author of the application, will be used if `authors` is not specified\
`contributors`: Additional contributors to the application\
`description`: The description of the application to show up in the Application Market\
`version`: The application version, preferably following the SemVer 2.0 specification\
`screenshots`: URLs to a location online starting with `https://` or `http://` for remote images or an identifier/resource location for local images\
`support`: See [SupportSchema.md](SupportSchema.md#schema-v1) v1

## Schema v1
`schemaVersion`: The schema version\
`name`: The name of the application\
`author`: The author of the application\
`description`: The description of the application to show up in the Application Market\
`version`: The application version, preferably following the SemVer 2.0 specification\
`screenshots`: URLs to a location online starting with `https://` or `http://` for remote images or an identifier/resource location for local images\
`support`: See [SupportSchema.md](SupportSchema.md#schema-v1) v1

## Schema v0
`schemaVersion`: The schema version\
`name`: The name of the application\
`author`: The author of the application\
`description`: The description of the application to show up in the Application Market\
`version`: The application version, preferably following the SemVer 2.0 specification\
`icon`: The identifier/resource location of the application's icon\
`screenshots`: URLs to a location online starting with `https://` or `http://` for remote images or an identifier/resource location for local images\
`support`: See [SupportSchema.md](SupportSchema.md#v1) v1