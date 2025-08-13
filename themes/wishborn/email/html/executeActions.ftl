<#ftl output_format="HTML">

<#-- Build the human-readable required actions list -->
<#assign requiredActionsText>
    <#if requiredActions??>
        <#list requiredActions><#items as a>${msg("requiredAction.${a}")}<#sep>, </#sep></#items></#list>
    </#if>
</#assign>

<#import "template.ftl" as layout>
<@layout.emailLayout>
    <style>
        body { font-family: 'Inter', Arial, sans-serif; background:#EEEEEE; margin:0; padding:20px; color:#4A4A4A; }
        .email-container { max-width:600px; margin:0 auto; background:#FFFFFF; border:1px solid #E5E7EB;
            border-radius:12px; box-shadow:0 4px 6px rgba(0,0,0,.05); overflow:hidden; }
        .email-header { background:#6985C0; padding:20px; text-align:center; }
        .email-logo { max-width:220px; height:auto; }
        .email-content { padding:30px; }
        .email-content a[href^="http"] { display:inline-block; background:#6985C0; color:#FFFFFF !important; text-decoration:none;
            font-weight:600; padding:12px 24px; border-radius:8px; margin:20px 0; text-align:center; }
        .email-footer { padding:20px; text-align:center; font-size:14px; color:#6B7280; background:#F9FAFB; border-top:1px solid #E5E7EB; }
    </style>

    <div class="email-container">
        <div class="email-header">
            <img class="email-logo" src="${properties.logoUrl!''}" alt="${realmName!''}">
        </div>

        <div class="email-content">
            ${kcSanitize(msg(
            "executeActionsBodyHtml",
            link,
            linkExpiration,
            realmName,
            requiredActionsText?has_content?then(requiredActionsText, msg("noRequiredActions")),
            linkExpirationFormatter(linkExpiration)
            ))?no_esc}
        </div>

        <div class="email-footer">
            <p>${realmName!''}</p>
        </div>
    </div>
</@layout.emailLayout>