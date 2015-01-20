/**
 * 
 * @author    Igor Misirev
 * @version   0.3
 * @since     12-12-2014
 * 
 * A Catalog of all {@link utfcatering.model.Article}s
 * 
 */
package utfcatering.model;

import org.salespointframework.catalog.Catalog;

import utfcatering.model.Article.Category;

public interface ArticleCatalog extends Catalog<Article> {
	Iterable<Article> findByCategory(Category category);
}
