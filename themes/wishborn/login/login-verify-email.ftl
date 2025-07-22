<#ftl output_format="HTML">
<#import "template.ftl" as kc>
<#setting boolean_format="c">

<!DOCTYPE html>
<html lang="${locale}"><#if direction??> dir="${direction}"</#if>
<head>
    <meta charset="utf-8"/>
    <title>${msg("emailVerifyTitle")} • ${realm.displayName!realm.name}</title>

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

<div class="bg-white border border-gray-200 shadow-sm rounded-xl
            w-full max-w-md p-8 flex flex-col gap-6 relative" style="background:#FFFFFF;">

    <!-- ─────────── LANGUAGE DROPDOWN ─────────── -->
    <#if realm.internationalizationEnabled && (locale.supported?size > 1)>
        <div id="kc-lang-switcher" class="absolute top-4 right-4">
            <button id="kc-lang-btn"
                    class="p-2 rounded-full bg-gray-100 hover:bg-gray-200
                     focus:outline-none focus:ring-2 focus:ring-offset-2
                     focus:ring-indigo-500">
                <!-- “translate” icon (Lucide-style) -->
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-languages-icon lucide-languages"><path d="m5 8 6 6"/><path d="m4 14 6-6 2-3"/><path d="M2 5h12"/><path d="M7 2h1"/><path d="m22 22-5-10-5 10"/><path d="M14 18h6"/></svg>
            </button>

            <ul id="kc-lang-menu"
                class="hidden fade-enter absolute right-0 mt-2 w-40 origin-top-right
                 bg-white border border-gray-200 rounded-md shadow-lg z-50" style="background:#FFFFFF;">
                <#list locale.supported as l>
                    <li>
                        <a href="${l.url}"
                           class="block px-3 py-2 text-sm hover:bg-gray-100
                      <#if l.languageTag == locale>
                         font-semibold text-indigo-600
                      </#if>">
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
    <!-- ───────────────────────────────────────── -->

    <!-- logo -->
    <div class="flex justify-center -mt-2 mb-4">
        <img src="${url.resourcesPath}/img/logoSmallNoBg.png"
             alt="Company logo" width="220" height="100"/>
    </div>

    <!-- heading -->
    <h1 class="text-2xl text-center text-[#465573] font-bold mb-4">
        ${msg("emailVerifyTitle")}
    </h1>

    <!-- main message -->
    <p class="text-[#4A4A4A] text-sm leading-6 mb-6 text-center">
        <#-- Same conditions as original template ------------------------- -->
        <#if verifyEmail??>
            ${msg("emailVerifyInstruction1", verifyEmail)}
        <#else>
            ${msg("emailVerifyInstruction4", user.email)}
        </#if>
    </p>

    <!-- resend / cancel buttons when called as AIA --------------------- -->
    <#if isAppInitiatedAction??>
        <form id="kc-verify-email-form" action="${url.loginAction}" method="post"
              class="flex flex-col gap-4 items-center">

            <#if verifyEmail??>
                <input type="submit" name="resend"
                       value="${msg("emailVerifyResend")}"
                       class="w-40 bg-gray-100 hover:bg-gray-200 text-[#465573]
                      font-semibold rounded-lg py-2 transition-colors"/>
            <#else>
                <input type="submit" name="send"
                       value="${msg("emailVerifySend")}"
                       class="w-40 bg-[#6985C0] hover:bg-[#637498] text-white
                      font-semibold rounded-lg py-2 transition-colors"/>
            </#if>

            <button type="submit" name="cancel-aia" value="true" formnovalidate
                    class="w-40 bg-white border border-gray-300 text-[#465573]
                     rounded-lg py-2 hover:bg-gray-50">
                ${msg("doCancel")}
            </button>
        </form>
    <#-- normal flow: show “click here” link ---------------------------- -->
    <#else>
        <p class="text-center text-sm text-[#4A4A4A]">
            ${msg("emailVerifyInstruction2")}<br/>
            <a href="${url.loginAction}" class="text-[#6985C0] font-semibold hover:underline">
                ${msg("doClickHere")}
            </a> ${msg("emailVerifyInstruction3")}
        </p>
    </#if>

</div>

</body>
</html>