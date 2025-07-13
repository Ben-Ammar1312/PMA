<#-- Only show when i18n is enabled in the realm -->
<#if realm.supportsInternationalization?default(false)
&& realm.internationalizationEnabled?default(false)>
    <div class="flex justify-end mb-2 items-center gap-2">
        <!-- Globe icon -->
        <svg xmlns="http://www.w3.org/2000/svg"
             class="w-6 h-6 text-[#465573]"
             fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <circle cx="12" cy="12" r="10" stroke-width="2"/>
            <path d="M2 12h20M12 2a15.3 15.3 0 0 1 0 20
               M12 2a15.3 15.3 0 0 0 0 20"
                  stroke-width="2"/>
        </svg>
        <form id="kc-locale-form" action="${url.loginAction}" method="post">
            <label for="kc-locale" class="sr-only">Choose language</label>
            <select id="kc-locale" name="kc_locale"
                    onchange="this.form.submit()"
                    class="border rounded px-2 py-1 text-sm">
                <#list realm.supportedLocales as loc>
                    <option value="${loc}"
                            <#if loc == locale>selected</#if>>
                        ${loc?upper_case}
                    </option>
                </#list>
            </select>
        </form>
    </div>
</#if>