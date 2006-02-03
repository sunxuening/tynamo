<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="trails" tagdir="/WEB-INF/tags" %>

<%@ attribute name="property" required="true" type="java.lang.Object" %>
<%@ attribute name="readOnly" %>

<c:choose>
	<c:when test="${property.propertyDescriptor.collection}">
		<trails:collection-property property="${property}"/>
	</c:when>
	<c:when test="${property.propertyDescriptor.date}">
		<trails:date-property property="${property}" readOnly="${readOnly}" />
	</c:when>
	<c:when test="${property.propertyDescriptor.boolean}">
		<trails:boolean-property property="${property}" readOnly="${readOnly}" />
	</c:when>
	<c:when test="${property.propertyDescriptor.numeric}">
		<trails:numeric-property property="${property}" readOnly="${readOnly}" />
	</c:when>
	<c:when test="${property.propertyDescriptor.string}">
		<trails:string-property property="${property}" readOnly="${readOnly}" />
	</c:when>
	<c:when test="${property.propertyDescriptor.objectReference}">
		<trails:object-reference-property property="${property}" readOnly="${readOnly}" />
	</c:when>
	<c:otherwise>
		
	</c:otherwise>
</c:choose>
