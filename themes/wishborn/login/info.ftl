<#ftl output_format="HTML">
<#import "template.ftl" as kc>
<#setting boolean_format="c">

<!DOCTYPE html>
<html lang="${locale}"<#if direction??> dir="${direction}"</#if>>
<head>
    <meta charset="utf-8"/>
    <title>${message.summary} • ${realm.displayName!realm.name}</title>

    <!-- Tailwind + Geist bundle -->
    <link rel="stylesheet" href="${url.resourcesPath}/css/styles.css">
    <!-- Inter fallback -->
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap">

    <style>
        html,body{background:#EEEEEE;font-family:'Geist','Inter',sans-serif}
        .fade-enter{opacity:0;transform:scale(.95)}
        .fade-enter-active{opacity:1;transform:scale(1);transition:all .15s ease}
    </style>
</head>

<body class="min-h-screen flex items-center justify-center">

<!-- White card wrapper -->
<div class="bg-white border border-gray-200 shadow-sm rounded-xl
            w-full max-w-md p-8 flex flex-col gap-6 relative" style="background:#FFFFFF">

    <!-- ─────── LANGUAGE DROPDOWN (optional) ─────── -->
    <#if realm.internationalizationEnabled && (locale.supported?size > 1)>
        <div id="kc-lang-switcher" class="absolute top-4 right-4">
            <button id="kc-lang-btn"
                    class="p-2 rounded-full bg-gray-100 hover:bg-gray-200
                     focus:outline-none focus:ring-2 focus:ring-offset-2
                     focus:ring-indigo-500">
                <!-- "translate" icon -->
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                     viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round">
                    <path d="m5 8 6 6"/><path d="m4 14 6-6 2-3"/>
                    <path d="M2 5h12"/><path d="M7 2h1"/>
                    <path d="m22 22-5-10-5 10"/><path d="M14 18h6"/>
                </svg>
            </button>

            <ul id="kc-lang-menu"
                class="hidden fade-enter absolute right-0 mt-2 w-40 origin-top-right bg-white
                       border border-gray-200 rounded-md shadow-lg z-50">
                <#list locale.supported as l>
                    <li class="bg-white first:rounded-t-md last:rounded-b-md">
                        <a href="${l.url}"
                           class="block px-3 py-2 text-sm bg-white hover:bg-gray-100
                                  <#if l.languageTag == locale>font-semibold text-indigo-600</#if>">
                            ${l.label}
                        </a>
                    </li>
                </#list>
            </ul>
        </div>

        <script>
            (() => {
                const btn  = document.getElementById('kc-lang-btn');
                const menu = document.getElementById('kc-lang-menu');
                btn.addEventListener('click', e => {
                    e.preventDefault();
                    menu.classList.toggle('hidden');
                    if (!menu.classList.contains('hidden')) {
                        menu.classList.add('fade-enter');
                        requestAnimationFrame(()=>menu.classList.add('fade-enter-active'));
                        setTimeout(()=>menu.classList.remove('fade-enter','fade-enter-active'),150);
                    }
                });
                document.addEventListener('click', e => {
                    if (!btn.contains(e.target) && !menu.contains(e.target))
                        menu.classList.add('hidden');
                });
            })();
        </script>
    </#if>

    <!-- logo -->
    <div class="flex justify-center -mt-2 mb-4">
        <img src="${url.resourcesPath}/img/logoSmallNoBg.png"
             alt="Company logo" width="220" height="100"/>
    </div>

    <!-- heading (message header or summary) -->
    <h1 class="text-2xl text-center text-[#465573] font-bold mb-2">
        <#if messageHeader??>
            ${kcSanitize(msg("${messageHeader}"))?no_esc}
        <#else>
            ${message.summary}
        </#if>
    </h1>

    <!-- main message & any required actions -->
    <p class="text-[#4A4A4A] text-sm leading-6 text-center mb-4">
        ${message.summary}
        <#if requiredActions??>
            <#list requiredActions>
                : <b><#items as reqActionItem>
                    ${kcSanitize(msg("requiredAction.${reqActionItem}"))?no_esc}
                    <#sep>, </#items></b>
            </#list>
        </#if>
    </p>

    <!-- navigation buttons / links -->
    <div class="flex flex-col gap-3 items-center">

        <#if pageRedirectUri?has_content>
            <a href="${pageRedirectUri}"
               class="w-40 bg-[#6985C0] hover:bg-[#637498] text-white
                      font-semibold rounded-lg py-2 text-center transition-colors">
                ${kcSanitize(msg("backToApplication"))?no_esc}
            </a>
        <#elseif actionUri?has_content>
            <a href="${actionUri}"
               class="w-40 bg-[#6985C0] hover:bg-[#637498] text-white
                      font-semibold rounded-lg py-2 text-center transition-colors">
                ${kcSanitize(msg("proceedWithAction"))?no_esc}
            </a>
        <#elseif (client.baseUrl)?has_content>
            <a href="${client.baseUrl}"
               class="w-40 bg-[#6985C0] hover:bg-[#637498] text-white
                      font-semibold rounded-lg py-2 text-center transition-colors">
                ${kcSanitize(msg("backToApplication"))?no_esc}
            </a>
        </#if>

        <!-- Always visible Back to Login -->
        <a href="144.91.76.149:3000/auth/login"
           class="w-40 bg-gray-100 hover:bg-gray-200 text-[#465573]
                  font-semibold rounded-lg py-2 text-center transition-colors border border-gray-300">
            ${kcSanitize(msg("loginBackToLogin"))?no_esc}
        </a>
    </div>

</div><!-- /wrapper -->

</body>
</html>