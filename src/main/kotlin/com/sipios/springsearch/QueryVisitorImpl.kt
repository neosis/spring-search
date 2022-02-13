package com.sipios.springsearch

import com.sipios.springsearch.anotation.SearchSpec
import com.sipios.springsearch.grammar.QueryBaseVisitor
import com.sipios.springsearch.grammar.QueryParser
import org.antlr.v4.runtime.atn.SemanticContext.and
import org.springframework.data.jpa.domain.Specifications

class QueryVisitorImpl<T>(private val searchSpecAnnotation: SearchSpec) : QueryBaseVisitor<Specifications<T>>() {
    private val ValueRegExp = Regex(pattern = "^(\\*?)(.+?)(\\*?)$")
    override fun visitOpQuery(ctx: QueryParser.OpQueryContext): Specifications<T> {
        val left = visit(ctx.left)
        val right = visit(ctx.right)

        return when (ctx.logicalOp.text) {
            "AND" -> left.and(right)
            "OR" -> left.or(right)
            else -> left.and(right)
//            else -> left.
        }
    }

    override fun visitPriorityQuery(ctx: QueryParser.PriorityQueryContext): Specifications<T> {
        return visit(ctx.query())
    }

    override fun visitAtomQuery(ctx: QueryParser.AtomQueryContext): Specifications<T> {
        return visit(ctx.criteria())
    }

    override fun visitInput(ctx: QueryParser.InputContext): Specifications<T> {
        return visit(ctx.query())
    }

    override fun visitCriteria(ctx: QueryParser.CriteriaContext): Specifications<T>? {
        val key = ctx.key()!!.text
        val op = ctx.op()!!.text
        var value = ctx.value()!!.text

        if (ctx.value().STRING() != null) {
            value = value
                .removeSurrounding("'")
                .removeSurrounding("\"")
                .replace("\\\"", "\"")
                .replace("\\'", "'")
        }

        val matchResult = this.ValueRegExp.find(value!!)
        val criteria = SearchCriteria(
            key,
            op,
            matchResult!!.groups[1]!!.value,
            matchResult.groups[2]!!.value,
            matchResult.groups[3]!!.value
        )
        var specification = SpecificationImpl<T>(criteria, searchSpecAnnotation)
        return Specifications.where(specification)
//        return SpecificationImpl(criteria, searchSpecAnnotation)
    }
}
