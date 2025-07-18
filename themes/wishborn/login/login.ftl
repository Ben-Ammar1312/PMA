<#ftl output_format="HTML">
<#import "template.ftl" as kc>
<#setting boolean_format="c">

<!DOCTYPE html>
<html lang="${locale}"><#if direction??>dir="${direction}"</#if>
<head>
  <meta charset="utf-8"/>
  <title>${msg("loginTitle")} • ${realm.displayName!realm.name}</title>

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
        <!-- “translate” icon -->
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
    ${msg("custom-welcome", "Bienvenue")}
  </h1>

  <!-- server-side flash message -->
  <#if message?? && message.summary?has_content>
    <div class="text-center text-red-600">${message.summary?no_esc}</div>
  </#if>

  <!-- ─────────── LOGIN FORM ─────────── -->
  <form id="kc-form-login" action="${url.loginAction}" method="post"
        class="flex flex-col gap-4">

    <!-- Username / Email -->
    <div>
      <label for="username" class="text-[#4A4A4A] font-semibold">
        <#-- choose correct built-in label -->
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
               autofocus/>
      </div>
    </div>

    <!-- Password -->
    <div>
      <label for="password" class="text-[#4A4A4A] font-semibold">
        ${msg("password")}
      </label>

      <div class="relative">
        <!-- lock icon -->
        <svg xmlns="http://www.w3.org/2000/svg"
             class="w-5 h-5 text-gray-500 absolute left-3 top-1/2 -translate-y-1/2"
             fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <rect x="5" y="11" width="14" height="8" rx="2" stroke-width="2"/>
          <path d="M12 16v-2" stroke-width="2"/>
          <path d="M8 11V8a4 4 0 0 1 8 0v3" stroke-width="2"/>
        </svg>

        <input id="password" name="password" type="password"
               placeholder="${msg('custom-placeholderPassword','********')}"
               class="pl-12 w-full border border-gray-300 rounded-md py-2
                      focus:border-[#6985C0] focus:ring-2 focus:ring-[#6985C0]"/>
      </div>

      <#if realm.resetPasswordAllowed>
        <div class="text-right mt-1">
          <a href="${url.loginResetCredentialsUrl}"
             class="text-sm text-[#6985C0] hover:underline">
            ${msg("doForgotPassword")}
          </a>
        </div>
      </#if>
    </div>

    <!-- Remember-me -->
    <#if realm.rememberMe>
      <div class="flex items-center gap-2 text-sm select-none">
        <input type="checkbox" id="rememberMe" name="rememberMe"
               class="accent-[#6985C0] h-4 w-4 rounded"
               <#if login.rememberMeChecked?? && login.rememberMeChecked>checked</#if>>
        <label for="rememberMe" class="text-[#4A4A4A] cursor-pointer">
          ${msg("rememberMe")}
        </label>
      </div>
    </#if>

    <!-- Submit -->
    <div class="flex justify-center">
      <button type="submit"
              class="mt-6 w-40 bg-[#6985C0] hover:bg-[#637498]
                     text-white font-semibold rounded-lg py-2 transition-colors">
        ${msg("doLogIn")}
      </button>
    </div>
    <#-- ─────  GOOGLE SIGN-IN  ───── -->
    <#if social?? && social.providers?has_content>
    <#-- show ONLY the provider whose alias is "google" -->
      <#list social.providers as p>
        <#if p.alias == "google">
          <div class="mt-6">
            <div class="flex items-center gap-3 text-xs text-gray-400">
              <div class="flex-grow border-t border-gray-300"></div>
              <span>${msg("or","or")}</span>
              <div class="flex-grow border-t border-gray-300"></div>
            </div>

            <a id="social-google"
               href="${p.loginUrl}"
               class="mt-4 w-full flex items-center justify-center gap-2
                  border border-gray-300 rounded-md py-2
                  hover:bg-gray-50 transition-colors">

              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"
                   class="h-5 w-5"><path fill="#EA4335" d="M24 9.5c3.8 0 7 .5 10 2.3l7.3-7.3C36.7 1.7 30.4 0 24 0 14.6 0 6 5 1.4 12.6l8.7 6.8C12.8 12.7 17.9 9.5 24 9.5z"/><path fill="#34A853" d="M46.4 24.5C46.4 23.2 46.3 22 46 20.8H24v7.9h12.5c-.6 3-2.5 5.6-5.4 7.3l8.7 6.7c5.1-4.5 8-11.2 8-18.2z"/><path fill="#4A90E2" d="M9.9 28.4c-.5-1.3-.8-2.7-.8-4.2s.3-2.9.8-4.2L1.1 13.2C0 15.6-.6 18.2-.6 20.9c0 2.7.6 5.3 1.7 7.8l8.8-6.8z"/><path fill="#FBBC05" d="M24 48c6.5 0 12.7-2.1 17.3-5.7l-8.7-6.7c-2.4 1.6-5.5 2.5-8.6 2.5-6.1 0-11.3-4-13.1-9.4l-8.8 6.8C6 42.9 14.6 48 24 48z"/></svg>

              <span class="text-sm font-medium">
            ${msg("identityProvider.google", "Sign in with Google")}
          </span>
            </a>
          </div>
        </#if>
      </#list>
    </#if>
    <#-- ──────────────────────────── -->

    <!-- Register hint -->
    <div class="flex justify-center space-x-1 text-sm mt-2">
      <span class="text-[#4A4A4A]">
        ${msg("custom-noAccount","Pas de compte ?")}
      </span>
      <a href="http://localhost:3000/register"
         class="text-[#6985C0] font-semibold hover:underline">
        ${msg("doRegister")}
      </a>
    </div>
  </form>
  <!-- ─────────────────────────────────── -->
</div>

</body>
</html>