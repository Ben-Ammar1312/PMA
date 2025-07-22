<#ftl output_format="HTML">
<#import "template.ftl" as kc>
<#setting boolean_format="c">

<!DOCTYPE html>
<html lang="${locale}"><#if direction??>dir="${direction}"</#if>
<head>
    <meta charset="utf-8"/>
    <title>${msg("emailForgotTitle")} • ${realm.displayName!realm.name}</title>

    <!-- Tailwind + Geist bundle -->
    <link rel="stylesheet" href="${url.resourcesPath}/css/styles.css">
    <!-- Inter fallback -->
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap">

    <style>
        html,body { background:#EEEEEE; font-family:'Geist','Inter',sans-serif }
        .fade-enter        { opacity:0; transform:scale(.95) }
        .fade-enter-active { opacity:1; transform:scale(1); transition:all .15s ease }
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
                <!-- "translate" icon -->
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-languages-icon lucide-languages"><path d="m5 8 6 6"/><path d="m4 14 6-6 2-3"/><path d="M2 5h12"/><path d="M7 2h1"/><path d="m22 22-5-10-5 10"/><path d="M14 18h6"/></svg>
            </button>

            <ul id="kc-lang-menu"
                class="hidden fade-enter absolute right-0 mt-2 w-40 origin-top-right bg-white
           border border-gray-200 rounded-md shadow-lg z-50" style="background:#FFFFFF;">
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
    <!-- ───────────────────────────────────────── -->

    <!-- logo -->
    <div class="flex justify-center -mt-2 mb-4">
        <img src="${url.resourcesPath}/img/logoSmallNoBg.png"
             alt="Company logo" width="220" height="100"/>
    </div>

    <!-- page heading -->
    <h1 class="text-2xl text-center text-[#465573] font-bold mb-4">
        ${msg("emailForgotTitle")}
    </h1>

    <!-- server-side flash message -->
    <#if message?? && message.summary?has_content>
        <div class="text-center text-red-600">${message.summary?no_esc}</div>
    </#if>

    <!-- ─────────── RESET PASSWORD FORM ─────────── -->
    <form id="kc-reset-password-form" action="${url.loginAction}" method="post"
          class="flex flex-col gap-4">

        <!-- Username / Email -->
        <div>
            <label for="username" class="text-[#4A4A4A] font-semibold">
                <#if !realm.loginWithEmailAllowed>
                    ${msg("username")}
                <#elseif !realm.registrationEmailAsUsername>
                    ${msg("usernameOrEmail")}
                <#else>
                    ${msg("email")}
                </#if>
            </label>

            <div class="relative">
                <!-- mail icon -->
                <svg xmlns="http://www.w3.org/2000/svg"
                     class="w-5 h-5 text-gray-500 absolute left-3 top-1/2 -translate-y-1/2"
                     fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <rect x="3" y="5" width="18" height="14" rx="2" stroke-width="2"/>
                    <path d="M3 7l9 6 9-6" stroke-width="2"/>
                </svg>

                <input id="username" name="username" type="text"
                       placeholder="${msg('custom-placeholderEmail','exemple@gmail.com')}"
                       class="pl-12 w-full border border-gray-300 rounded-md py-2
                      focus:border-[#6985C0] focus:ring-2 focus:ring-[#6985C0]"
                       autofocus
                       value="${(auth.attemptedUsername!'')}"
                       aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"/>

                <#if messagesPerField.existsError('username')>
                    <span id="input-error-username" class="text-sm text-red-600 mt-1" aria-live="polite">
            ${kcSanitize(messagesPerField.get('username'))?no_esc}
          </span>
                </#if>
            </div>
        </div>

        <!-- Submit -->
        <div class="flex justify-center">
            <button type="submit"
                    class="mt-6 w-40 bg-[#6985C0] hover:bg-[#637498]
                     text-white font-semibold rounded-lg py-2 transition-colors">
                ${msg("doSubmit")}
            </button>
        </div>

        <!-- Back to login link -->
        <div class="flex justify-center space-x-1 text-sm mt-2">
            <a href="${url.loginUrl}"
               class="text-[#6985C0] font-semibold hover:underline">
                ${kcSanitize(msg("backToLogin"))?no_esc}
            </a>
        </div>
    </form>
    <!-- ─────────────────────────────────── -->

    <!-- Info text -->
    <#if realm.duplicateEmailsAllowed>
        <div class="text-sm text-[#4A4A4A] text-center">
            ${msg("emailInstructionUsername")}
        </div>
    <#else>
        <div class="text-sm text-[#4A4A4A] text-center">
            ${msg("emailInstruction")}
        </div>
    </#if>
</div>

</body>
</html>