package com.cn.dao.util;

import java.util.LinkedList;
import java.util.List;

public final class QueryParam {

	public final static String AND = "and";

	public final static String OR = "or";

	public final static String NOT = "not";

	public final static String OPERATOR_EQ = "=";

	public final static String OPERATOR_NE = "!=";

	public final static String OPERATOR_NE_ANSINULL_OFF = "!=%";

	public final static String OPERATOR_GE = ">=";

	public final static String OPERATOR_GT = ">";

	public final static String OPERATOR_NGE = "!>=";

	public final static String OPERATOR_NGT = "!>";

	public final static String OPERATOR_LE = "<=";

	public final static String OPERATOR_LT = "<";

	public final static String OPERATOR_NLE = "!<=";

	public final static String OPERATOR_NLT = "!<";

	public final static String OPERATOR_LIKE = "like";

	public final static String OPERATOR_NLIKE = "!like";

	public final static String OPERATOR_INCLUDE = "include";

	public final static String OPERATOR_NINCLUDE = "!include";

	public final static String OPERATOR_ILIKE = "ilike";

	public final static String OPERATOR_NILIKE = "!ilike";

	public final static String OPERATOR_IINCLUDE = "iinclude";

	public final static String OPERATOR_NIINCLUDE = "!iinclude";

	public final static String OPERATOR_IS = "is";

	public final static String OPERATOR_NIS = "!is";

	public final static String OPERATOR_IN = "in";

	public final static String OPERATOR_NIN = "!in";

	public final static String FETCH = "fetch";

	public final static int BASIC = 1;

	public final static int ADVANCED = 2;

	private String name;

	private Object value;

	private int type;

	private String operator = OPERATOR_EQ;

	private String sql;

	private int queryMode;

	private List<QueryParam> andParams;
	private List<QueryParam> orParams;
	private List<QueryParam> notParams;

	public QueryParam(String sql) {
		this.queryMode = BASIC;
		this.sql = sql;
	}

	public QueryParam(String name, String operator, Object value, int type) {
		this.queryMode = BASIC;
		setName(name);
		setOperator(operator);
		setValue(value);
		this.type = type;
		checkOperatorForNullValue();
	}

	public QueryParam(String name, String operator, Object value) {
		this.queryMode = BASIC;
		setName(name);
		setOperator(operator);
		setValue(value);
		this.type = -1;
		checkOperatorForNullValue();
	}

	public QueryParam(String name, Object value, int type) {
		this.queryMode = BASIC;
		setName(name);
		setValue(value);
		this.type = type;
		checkOperatorForNullValue();
	}

	public QueryParam(String name, Object value) {
		this.queryMode = BASIC;
		setName(name);
		setValue(value);
		this.type = -1;
		checkOperatorForNullValue();
	}

	public QueryParam() {
	}

	public void and(QueryParam queryParam) {
		if (this.queryMode == BASIC) {
			throw new RuntimeException(
					"Current QueryParam was set as BASIC mode, can not be added a QueryParam!");
		}
		this.queryMode = ADVANCED;
		if (andParams == null) {
			andParams = new LinkedList<QueryParam>();
		}
		andParams.add(queryParam);
	}

	public void or(QueryParam queryParam) {
		if (this.queryMode == BASIC) {
			throw new RuntimeException(
					"Current QueryParam was set as BASIC mode, can not be added a QueryParam!");
		}
		this.queryMode = ADVANCED;
		if (orParams == null) {
			orParams = new LinkedList<QueryParam>();
		}
		orParams.add(queryParam);
	}

	public void not(QueryParam queryParam) {
		if (this.queryMode == BASIC) {
			throw new RuntimeException(
					"Current QueryParam was set as BASIC mode, can not be added a QueryParam!");
		}
		this.queryMode = ADVANCED;
		if (notParams == null) {
			notParams = new LinkedList<QueryParam>();
		}
		notParams.add(queryParam);
	}

	public String getName() {
		return name;
	}

	public String getOperator() {
		return operator;
	}

	public int getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public String getSql() {
		return sql;
	}

	private void setName(String name) {
		if (name == null) {
			throw new RuntimeException("Parameter name can not be NULL!");
		}
		this.name = name.trim();
	}

	private void setOperator(String operator) {
		if (operator == null) {
			throw new RuntimeException("Operator can not be NULL!");
		}
		this.operator = operator.trim();
		validateOperator();
		transferOperator();
	}

	private void setValue(Object value) {
		this.value = value;
		transferOperator();
	}

	private void transferOperator() {
		if (value == null) {
			if (OPERATOR_EQ.equals(this.operator)) {
				this.operator = OPERATOR_IS;
			} else if (OPERATOR_NE.equals(this.operator)) {
				this.operator = OPERATOR_NIS;
			}
		} else {
			if (OPERATOR_IS.equals(this.operator)) {
				this.operator = OPERATOR_EQ;
			} else if (OPERATOR_NIS.equals(this.operator)) {
				this.operator = OPERATOR_NE;
			}
		}
	}

	private void checkOperatorForNullValue() {
		if (this.value == null) {
			if (!(OPERATOR_IS.equals(this.operator) || OPERATOR_NIS
					.equals(this.operator))) {
				throw new RuntimeException(
						"The operator can only be set 'is' or '=' or 'is not' or '!=' when value is NULL!");
			}
		} else if (OPERATOR_IS.equals(this.operator)
				|| OPERATOR_NIS.equals(this.operator)) {
			throw new RuntimeException(
					"The operator 'is' or 'is not' is available only when value is NULL!");
		}
	}

	private void validateOperator() {
		if (this.operator.startsWith("!")) {
			if (this.operator.endsWith("%"))
				return;
			if (OPERATOR_NE.equals(this.operator))
				return;
			if (OPERATOR_NGE.equals(this.operator))
				return;
			if (OPERATOR_NGT.equals(this.operator))
				return;
			if (OPERATOR_NLE.equals(this.operator))
				return;
			if (OPERATOR_NLT.equals(this.operator))
				return;
			if (OPERATOR_NLIKE.equals(this.operator))
				return;
			if (OPERATOR_NINCLUDE.equals(this.operator))
				return;
			if (OPERATOR_NILIKE.equals(this.operator))
				return;
			if (OPERATOR_NIINCLUDE.equals(this.operator))
				return;
			if (OPERATOR_NIS.equals(this.operator))
				return;
			if (OPERATOR_NIN.equals(this.operator))
				return;
		} else {
			if (OPERATOR_EQ.equals(this.operator))
				return;
			if (OPERATOR_GE.equals(this.operator))
				return;
			if (OPERATOR_GT.equals(this.operator))
				return;
			if (OPERATOR_LE.equals(this.operator))
				return;
			if (OPERATOR_LT.equals(this.operator))
				return;
			if (OPERATOR_LIKE.equals(this.operator))
				return;
			if (OPERATOR_INCLUDE.equals(this.operator))
				return;
			if (OPERATOR_ILIKE.equals(this.operator))
				return;
			if (OPERATOR_IINCLUDE.equals(this.operator))
				return;
			if (OPERATOR_IS.equals(this.operator))
				return;
			if (OPERATOR_IN.equals(this.operator))
				return;
			if (FETCH.equals(this.operator))
				return;
		}
		throw new RuntimeException("The operator " + this.operator
				+ " could be incorrect!");
	}

	public int getMode() {
		return queryMode;
	}

	public List<QueryParam> getAndParams() {
		return andParams;
	}

	public List<QueryParam> getNotParams() {
		return notParams;
	}

	public List<QueryParam> getOrParams() {
		return orParams;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		if (queryMode == BASIC) {
			if (sql != null) {
				sb.append(sql);
			} else {
				sb.append(name).append(' ').append(operator).append(' ')
						.append(value);
			}
		} else {
			if (andParams != null && andParams.size() > 0) {
				boolean firstFlag = true;
				for (QueryParam q : andParams) {
					if (firstFlag) {
						firstFlag = false;
					} else {
						sb.append(" and ");
					}
					sb.append(q.toString());
				}
			}
			if (orParams != null && orParams.size() > 0) {
				if (sb.length() > 1) {
					sb.append(" or ");
				}
				boolean firstFlag = true;
				for (QueryParam q : orParams) {
					if (firstFlag) {
						firstFlag = false;
					} else {
						sb.append(" or ");
					}
					sb.append(q.toString());
				}
			}
			if (notParams != null && notParams.size() > 0) {
				if (sb.length() > 0) {
					sb.append(" and ");
				}
				boolean firstFlag = true;
				for (QueryParam q : notParams) {
					if (firstFlag) {
						firstFlag = false;
					} else {
						sb.append(" and ");
					}
					sb.append("not ");
					sb.append(q.toString());
				}
			}
		}
		sb.append(')');
		return sb.toString();
	}

}
