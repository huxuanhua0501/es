package net.learn.es.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * elasticsearch 查询帮助类
 *
 * @author shoumiao_yao
 * @date 2016-09-20
 */
public class SearchHelper {
    private Logger logger = LoggerFactory.getLogger(SearchHelper.class);
    private Integer start = 0;
    private Integer limit = 10;
    private SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    private List<QueryBuilder> mustQueryS = new ArrayList<QueryBuilder>();
    private List<QueryBuilder> mustNotQueryS = new ArrayList<QueryBuilder>();
    private List<QueryBuilder> filterQueryS = new ArrayList<QueryBuilder>();
    private List<QueryBuilder> shouldQueryS = new ArrayList<QueryBuilder>();

    /**
     * and 查询
     * @param querys
     * @param isClear
     * @return
     */
    public SearchHelper must(Boolean isClear,QueryBuilder... querys) {
        if(isClear) this.mustQueryS.clear();
        if (querys==null) return this;
        this.mustQueryS.addAll(Arrays.asList(querys));
        return this;
    }

    /**
     * 否定
     * @param querys
     * @param isClear
     * @return
     */
    public SearchHelper mustNot(Boolean isClear,QueryBuilder... querys) {
        if(isClear) this.mustNotQueryS.clear();
        if (querys==null) return this;
        this.mustNotQueryS.addAll(Arrays.asList(querys));
        return this;
    }

    /**
     * 过滤
     * @param querys
     * @param isClear
     * @return
     */
    public SearchHelper filter(Boolean isClear,QueryBuilder... querys) {
        if(isClear) this.filterQueryS.clear();
        if (querys==null) return this;
        this.filterQueryS.addAll(Arrays.asList(querys));
        return this;
    }

    /**
     * 或者
     * @param querys
     * @param isClear
     * @return
     */
    public SearchHelper should(Boolean isClear,QueryBuilder... querys) {
        if(isClear) this.shouldQueryS.clear();
        if (querys==null) return this;
        this.shouldQueryS.addAll(Arrays.asList(querys));
        return this;
    }

    /**
     * 清空查询条件
     */
    public void empty() {
        this.mustQueryS.clear();
        this.mustNotQueryS.clear();
        this.filterQueryS.clear();
        this.shouldQueryS.clear();
    }

    public BoolQueryBuilder buildBoolQueryBuilder(){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (QueryBuilder builder : mustQueryS) {
            boolQueryBuilder.must(builder);
        }
        for (QueryBuilder builder : mustNotQueryS) {
            boolQueryBuilder.mustNot(builder);
        }
        for (QueryBuilder builder : filterQueryS) {
            boolQueryBuilder.filter(builder);
        }
        for (QueryBuilder builder : shouldQueryS) {
            boolQueryBuilder.should(builder);
        }
        return boolQueryBuilder;
    }

    /**
     * 构建SearchRequest对象
     *
     * @return
     */
    public SearchSourceBuilder build() {
        BoolQueryBuilder boolQueryBuilder=buildBoolQueryBuilder();
        this.sourceBuilder.query(boolQueryBuilder);
        logger.info("query build:{}", boolQueryBuilder);
        return this.sourceBuilder;
    }


    /**
     * 域相等
     *
     * @param field
     * @param value
     */
    public SearchHelper eq(String field, Object value) {
        if (StringUtils.isEmpty(field) || value == null) return this;
        TermQueryBuilder builder = QueryBuilders.termQuery(field, value);
        this.mustQueryS.add(builder);
        return this;
    }

    /**
     * in
     *
     * @param field
     * @param value
     */
    public SearchHelper in(String field, Object[] value) {
        if (StringUtils.isEmpty(field) || ArrayUtils.isEmpty(value)) return this;
        TermsQueryBuilder builder = QueryBuilders.termsQuery(field, value);
        this.mustQueryS.add(builder);
        return this;
    }


    /**
     * 域存在
     *
     * @param field
     */
    public SearchHelper exist(String field) {
        if (StringUtils.isEmpty(field)) return this;
        ExistsQueryBuilder builder = QueryBuilders.existsQuery(field);
        this.mustQueryS.add(builder);
        return this;
    }

    /**
     * 批量id查找
     *
     * @param ids
     */
    public SearchHelper ids(String... ids) {
        if (ArrayUtils.isEmpty(ids)) return this;
        IdsQueryBuilder builder = QueryBuilders.idsQuery().addIds(ids);
        this.mustQueryS.add(builder);
        return this;
    }

    /**
     * 大于 小于
     *
     * @param field
     * @param from
     * @param end
     */
    public SearchHelper gtlt(String field, Object from, Object end) {
        if (StringUtils.isEmpty(field)) return this;
        if (from == null && end == null) return this;
        RangeQueryBuilder builder = QueryBuilders.rangeQuery(field);
        if (from != null) {
            builder.gt(from);
        }
        if (from != null) {
            builder.lt(end);
        }
        this.mustQueryS.add(builder);
        return this;
    }

    /**
     * 大于  小于等于
     *
     * @param field
     * @param from
     * @param end
     */
    public SearchHelper gtlte(String field, Object from, Object end) {
        if (StringUtils.isEmpty(field)) return this;
        if (from == null && end == null) return this;
        RangeQueryBuilder builder = QueryBuilders.rangeQuery(field);
        if (from != null) {
            builder.gt(from);
        }
        if (end != null) {
            builder.lte(end);
        }
        this.mustQueryS.add(builder);
        return this;
    }

    /**
     * 大于等于 小于
     *
     * @param field
     * @param from
     * @param end
     */
    public SearchHelper gtelt(String field, Object from, Object end) {
        if (StringUtils.isEmpty(field)) return this;
        if (from == null && end == null) return this;
        RangeQueryBuilder builder = QueryBuilders.rangeQuery(field);
        if (from != null) {
            builder.gte(from);
        }
        if (end != null) {
            builder.lt(end);
        }
        this.mustQueryS.add(builder);
        return this;
    }

    /**
     * 大于等于 小于等于
     *
     * @param field
     * @param from
     * @param end
     */
    public SearchHelper gtelte(String field, Object from, Object end) {
        if (StringUtils.isEmpty(field)) return this;
        if (from == null && end == null) return this;
        RangeQueryBuilder builder = QueryBuilders.rangeQuery(field);
        if (from != null) {
            builder.gte(from);
        }
        if (end != null) {
            builder.lte(end);
        }
        this.mustQueryS.add(builder);
        return this;
    }

    /**
     * 前缀 prefix
     *
     * @param field
     * @param prefix
     */
    public SearchHelper prefix(String field, String prefix) {
        if (StringUtils.isEmpty(field) || StringUtils.isEmpty(prefix)) return this;
        PrefixQueryBuilder builder = QueryBuilders.prefixQuery(field, prefix);
        this.mustQueryS.add(builder);
        return this;
    }

    /**
     * 通配符 wildcard
     *
     * @param field
     * @param wildcard
     */
    public SearchHelper wildcard(String field, String wildcard) {
        if (StringUtils.isEmpty(field) || StringUtils.isEmpty(wildcard)) return this;
        WildcardQueryBuilder builder = QueryBuilders.wildcardQuery(field, wildcard);
        this.mustQueryS.add(builder);
        return this;
    }

    /**
     * 模糊 fuzzy
     *
     * @param field
     * @param fuzzy
     */
    public SearchHelper fuzzy(String field, String fuzzy) {
        if (StringUtils.isEmpty(field) || StringUtils.isEmpty(fuzzy)) return this;
        FuzzyQueryBuilder builder = QueryBuilders.fuzzyQuery(field, fuzzy);
        this.mustQueryS.add(builder);
        return this;
    }

    /**
     * 模糊 regesp
     *
     * @param field
     * @param regexp
     */
    public SearchHelper regexp(String field, String regexp) {
        if (StringUtils.isEmpty(field) || StringUtils.isEmpty(regexp)) return this;
        RegexpQueryBuilder builder = QueryBuilders.regexpQuery(field, regexp);
        this.mustQueryS.add(builder);
        return this;
    }

    /**
     * 单个属性匹配
     * @param field
     * @param value
     * @return
     */
    public SearchHelper match(String field,Object value){
        MatchQueryBuilder matchQueryBuilder=QueryBuilders.matchQuery(field, value);
//        matchQueryBuilder.type(MatchQueryBuilder.Type.PHRASE);
//        matchQueryBuilder.EMPTY_PARAMS.;
        this.mustQueryS.add(matchQueryBuilder);
        return this;
    }

    /**
     * 多个属性匹配
     * @param value
     * @param fields
     * @return
     */
    public SearchHelper multiTermMatch(Object value,String... fields){
        MultiMatchQueryBuilder multiMatchQueryBuilder=QueryBuilders.multiMatchQuery(value, fields);
        this.mustQueryS.add(multiMatchQueryBuilder);
        return this;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer start() {
        return this.start;
    }

    public Integer limit() {
        return this.limit;
    }

}
