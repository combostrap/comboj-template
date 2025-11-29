# Thymeleaf note


# Syntax
[Doc](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#standard-expression-syntax)

  * Variable Expressions: `${key}` - will return the value of the key in the map
  * Message Expressions: `#{...}` for localized text called `messages`. [Doc](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#a-multi-language-welcome)



## Xml Template

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<urlset xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd"
        xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:th="http://www.thymeleaf.org">
	<url th:each="page : ${pages}">
		<loc th:text="${page.url}">http://path/to/page</loc>
		<lastmod th:text="${page.lastModified}" th:unless="${#strings.isEmpty(page.lastModified)}">Last Modified</lastmod>
		<changefreq th:text="${page.changeFrequency}" th:unless="${#strings.isEmpty(page.changeFrequency)}">Change Frequency</changefreq>
		<priority th:text="${page.priority}" th:unless="${#strings.isEmpty(page.priority)}">Priority</priority>
	</url>
</urlset>
```

## Resolver

Documentation: https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#template-resolvers
