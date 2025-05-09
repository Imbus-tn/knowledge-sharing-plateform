<template>
    <!-- Hero Section with Tech Background -->
    <div class="relative min-h-screen">
      <div class="absolute inset-0 z-0">
        <div class="absolute inset-0 bg-gradient-to-br from-slate-900 via-slate-900 to-emerald-900/20"></div>
        <div class="absolute inset-0 bg-[radial-gradient(circle_at_30%_50%,rgba(16,185,129,0.1),transparent_50%)]"></div>
        <!-- Animated Particles Background -->
        <div class="absolute inset-0 overflow-hidden">
          <div class="absolute w-4 h-4 bg-emerald-500/10 rounded-full animate-pulse-slow" style="top: 20%; left: 30%"></div>
          <div class="absolute w-6 h-6 bg-emerald-500/10 rounded-full animate-pulse-slow" style="top: 60%; left: 70%"></div>
          <div class="absolute w-3 h-3 bg-emerald-500/10 rounded-full animate-pulse-slow" style="top: 40%; left: 50%"></div>
        </div>
      </div>
  
      <div class="relative z-10 max-w-[1440px] mx-auto px-4 sm:px-6 lg:px-4 py-16">
        <!-- Hero Content -->
        <div class="max-w-4xl mx-auto text-center">
          <div class="inline-flex items-center px-4 py-2 bg-emerald-500/10 rounded-full mb-8">
            <Sparkles class="w-5 h-5 text-emerald-500 mr-2" />
            <span class="text-emerald-500 font-medium">Revolutionizing Team Knowledge Sharing</span>
          </div>
          <h1 class="text-4xl md:text-6xl font-bold text-white mb-8 leading-tight">
            Transform Your Team's <span class="text-emerald-500">Knowledge</span> Into Power
          </h1>
          <p class="text-slate-300 text-xl md:text-2xl mb-12 mx-auto max-w-3xl leading-relaxed">
            Build a culture of continuous learning and collaboration. Share expertise, grow together, and drive innovation across your organization.
          </p>
          <div class="flex flex-col sm:flex-row items-center justify-center gap-4">
            <router-link to="/login" 
              class="inline-flex items-center px-8 py-4 bg-emerald-500 text-white rounded-lg text-lg font-medium hover:bg-emerald-600 transform hover:scale-105 transition-all duration-200 shadow-lg hover:shadow-emerald-500/20 w-full sm:w-auto justify-center">
              Get Started Now
              <ArrowRight class="ml-2 w-6 h-6" />
            </router-link>
            <a href="#features" 
              class="inline-flex items-center px-8 py-4 bg-slate-700/50 text-white rounded-lg text-lg font-medium hover:bg-slate-600/50 transition-all duration-200 w-full sm:w-auto justify-center">
              Explore Features
            </a>
          </div>
          
          <!-- Trust Badges -->
          <div class="mt-16 flex flex-wrap items-center justify-center gap-8">
            <div class="flex items-center space-x-2 text-slate-400">
              <Shield class="w-5 h-5" />
              <span>Enterprise-grade Security</span>
            </div>
            <div class="flex items-center space-x-2 text-slate-400">
              <Users class="w-5 h-5" />
              <span>5000+ Active Users</span>
            </div>
            <div class="flex items-center space-x-2 text-slate-400">
              <Award class="w-5 h-5" />
              <span>98% Satisfaction Rate</span>
            </div>
          </div>
        </div>
  
        <!-- Performance Metrics Section -->
        <div class="mt-24 grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
          <div class="space-y-8">
            <div class="inline-flex items-center px-4 py-2 bg-emerald-500/10 rounded-full">
              <TrendingUp class="w-5 h-5 text-emerald-500 mr-2" />
              <span class="text-emerald-500 font-medium">Measurable Results</span>
            </div>
            <h2 class="text-3xl font-bold text-white">Boost Team Performance with Knowledge Sharing</h2>
            <p class="text-slate-300 text-lg">
              Our platform has helped organizations achieve remarkable improvements in team productivity, innovation, and employee satisfaction.
            </p>
            
            <div class="space-y-4">
              <div class="flex items-center space-x-4">
                <div class="w-12 h-12 rounded-full bg-emerald-500/10 flex items-center justify-center">
                  <Zap class="w-6 h-6 text-emerald-500" />
                </div>
                <div>
                  <h3 class="text-xl font-semibold text-white">37% Faster Onboarding</h3>
                  <p class="text-slate-400">New team members reach productivity faster with centralized knowledge</p>
                </div>
              </div>
              
              <div class="flex items-center space-x-4">
                <div class="w-12 h-12 rounded-full bg-emerald-500/10 flex items-center justify-center">
                  <Clock class="w-6 h-6 text-emerald-500" />
                </div>
                <div>
                  <h3 class="text-xl font-semibold text-white">25% Time Saved</h3>
                  <p class="text-slate-400">Reduce time spent searching for information and solutions</p>
                </div>
              </div>
              
              <div class="flex items-center space-x-4">
                <div class="w-12 h-12 rounded-full bg-emerald-500/10 flex items-center justify-center">
                  <Lightbulb class="w-6 h-6 text-emerald-500" />
                </div>
                <div>
                  <h3 class="text-xl font-semibold text-white">42% More Innovation</h3>
                  <p class="text-slate-400">Teams that share knowledge generate more innovative ideas</p>
                </div>
              </div>
            </div>
          </div>
          
          <div class="relative">
            <!-- Modern Performance Chart -->
            <div class="bg-slate-800/80 backdrop-blur-sm rounded-xl border border-slate-700 p-6 shadow-xl">
              <h3 class="text-xl font-semibold text-white mb-6">Knowledge Sharing Impact</h3>
              
              <!-- Modern Chart Area -->
              <div class="relative h-64 mb-6">
                <!-- Chart Grid -->
                <div class="absolute inset-0 flex flex-col justify-between">
                  <div class="border-b border-slate-700/30 h-0"></div>
                  <div class="border-b border-slate-700/30 h-0"></div>
                  <div class="border-b border-slate-700/30 h-0"></div>
                  <div class="border-b border-slate-700/30 h-0"></div>
                  <div class="border-b border-slate-700/30 h-0"></div>
                </div>
                
                <!-- Y-axis Labels -->
                <div class="absolute left-0 inset-y-0 flex flex-col justify-between text-xs text-slate-500 -ml-6">
                  <div>100%</div>
                  <div>75%</div>
                  <div>50%</div>
                  <div>25%</div>
                  <div>0%</div>
                </div>
                
                <!-- X-axis Labels -->
                <div class="absolute bottom-0 inset-x-0 flex justify-between text-xs text-slate-500 mt-2">
                  <div>Q1</div>
                  <div>Q2</div>
                  <div>Q3</div>
                  <div>Q4</div>
                </div>
                
                <!-- Chart Data Area -->
                <div class="absolute inset-0 mt-2">
                  <!-- Gradient Background for Chart -->
                  <div class="absolute bottom-0 left-0 right-0 h-3/4 bg-gradient-to-t from-emerald-500/10 to-transparent rounded-md"></div>
                  
                  <!-- Line Chart - Before Implementation -->
                  <svg class="absolute inset-0 h-full w-full" viewBox="0 0 100 100" preserveAspectRatio="none">
                    <path 
                      d="M0,80 C10,75 20,70 30,65 C40,60 50,62 60,58 C70,54 80,50 100,45" 
                      fill="none" 
                      stroke="#ef4444" 
                      stroke-width="2"
                      stroke-dasharray="1000"
                      stroke-dashoffset="1000"
                      class="opacity-50"
                    >
                      <animate 
                        attributeName="stroke-dashoffset" 
                        from="1000" 
                        to="0" 
                        dur="1.5s" 
                        begin="0.2s"
                        fill="freeze" 
                      />
                    </path>
                  </svg>
                  
                  <!-- Line Chart - After Implementation -->
                  <svg class="absolute inset-0 h-full w-full" viewBox="0 0 100 100" preserveAspectRatio="none">
                    <path 
                      d="M0,80 C10,75 20,65 30,50 C40,35 50,25 60,20 C70,15 80,12 100,10" 
                      fill="none" 
                      stroke="#10b981" 
                      stroke-width="3"
                      stroke-dasharray="1000"
                      stroke-dashoffset="1000"
                    >
                      <animate 
                        attributeName="stroke-dashoffset" 
                        from="1000" 
                        to="0" 
                        dur="2s" 
                        fill="freeze" 
                      />
                    </path>
                    
                    <!-- Data Points -->
                    <circle cx="0" cy="80" r="3" fill="#10b981" opacity="0">
                      <animate attributeName="opacity" from="0" to="1" dur="0.3s" begin="2s" fill="freeze" />
                    </circle>
                    <circle cx="30" cy="50" r="3" fill="#10b981" opacity="0">
                      <animate attributeName="opacity" from="0" to="1" dur="0.3s" begin="2.2s" fill="freeze" />
                    </circle>
                    <circle cx="60" cy="20" r="3" fill="#10b981" opacity="0">
                      <animate attributeName="opacity" from="0" to="1" dur="0.3s" begin="2.4s" fill="freeze" />
                    </circle>
                    <circle cx="100" cy="10" r="3" fill="#10b981" opacity="0">
                      <animate attributeName="opacity" from="0" to="1" dur="0.3s" begin="2.6s" fill="freeze" />
                    </circle>
                  </svg>
                  
                  <!-- Area Chart Fill -->
                  <svg class="absolute inset-0 h-full w-full" viewBox="0 0 100 100" preserveAspectRatio="none">
                    <defs>
                      <linearGradient id="areaGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                        <stop offset="0%" stop-color="#10b981" stop-opacity="0.3" />
                        <stop offset="100%" stop-color="#10b981" stop-opacity="0.05" />
                      </linearGradient>
                    </defs>
                    <path 
                      d="M0,80 C10,75 20,65 30,50 C40,35 50,25 60,20 C70,15 80,12 100,10 L100,100 L0,100 Z" 
                      fill="url(#areaGradient)"
                      opacity="0"
                    >
                      <animate 
                        attributeName="opacity" 
                        from="0" 
                        to="1" 
                        dur="1s" 
                        begin="2s"
                        fill="freeze" 
                      />
                    </path>
                  </svg>
                </div>
                
                <!-- Legend -->
                <div class="absolute top-0 right-0 flex items-center space-x-4">
                  <div class="flex items-center">
                    <div class="w-3 h-3 bg-red-500 opacity-50 rounded-full mr-1"></div>
                    <span class="text-xs text-slate-400">Before</span>
                  </div>
                  <div class="flex items-center">
                    <div class="w-3 h-3 bg-emerald-500 rounded-full mr-1"></div>
                    <span class="text-xs text-slate-400">With Imbus</span>
                  </div>
                </div>
              </div>
              
              <!-- Key Metrics -->
              <div class="grid grid-cols-3 gap-4">
                <div class="text-center p-3 bg-slate-700/50 rounded-lg">
                  <div class="text-2xl font-bold text-emerald-500">+78%</div>
                  <div class="text-xs text-slate-400">Knowledge Retention</div>
                </div>
                <div class="text-center p-3 bg-slate-700/50 rounded-lg">
                  <div class="text-2xl font-bold text-emerald-500">-32%</div>
                  <div class="text-xs text-slate-400">Support Tickets</div>
                </div>
                <div class="text-center p-3 bg-slate-700/50 rounded-lg">
                  <div class="text-2xl font-bold text-emerald-500">+65%</div>
                  <div class="text-xs text-slate-400">Team Collaboration</div>
                </div>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Communication & Collaboration Section -->
        <div class="mt-32 grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div class="order-2 lg:order-1 relative">
            <div class="relative rounded-2xl overflow-hidden">
              <img 
                src="../assets/Team-image.jpeg"
                alt="Team Communication"
                class="w-full h-[500px] object-cover rounded-2xl"
              />
              <div class="absolute inset-0 bg-gradient-to-t from-slate-900 via-slate-900/20 to-transparent"></div>
            </div>
            
            <!-- Floating Communication Elements -->
            <div class="absolute top-1/4 -left-8 bg-slate-800/90 backdrop-blur-sm rounded-xl p-4 shadow-lg border border-slate-700 max-w-[200px]">
              <div class="flex items-start space-x-2">
                <div class="w-8 h-8 rounded-full bg-emerald-500 flex items-center justify-center flex-shrink-0">
                  <span class="text-white font-medium">JD</span>
                </div>
                <div>
                  <p class="text-white text-sm">Just shared a new tutorial on Vue 3 Composition API!</p>
                  <p class="text-xs text-slate-400 mt-1">2 min ago</p>
                </div>
              </div>
            </div>
            
            <div class="absolute bottom-1/4 -right-8 bg-slate-800/90 backdrop-blur-sm rounded-xl p-4 shadow-lg border border-slate-700 max-w-[200px]">
              <div class="flex items-start space-x-2">
                <div class="w-8 h-8 rounded-full bg-blue-500 flex items-center justify-center flex-shrink-0">
                  <span class="text-white font-medium">SK</span>
                </div>
                <div>
                  <p class="text-white text-sm">Great resource! This will help our frontend team.</p>
                  <p class="text-xs text-slate-400 mt-1">Just now</p>
                </div>
              </div>
            </div>
          </div>
          
          <div class="order-1 lg:order-2 space-y-8">
            <div class="inline-flex items-center px-4 py-2 bg-emerald-500/10 rounded-full">
              <MessageSquare class="w-5 h-5 text-emerald-500 mr-2" />
              <span class="text-emerald-500 font-medium">Seamless Communication</span>
            </div>
            <h2 class="text-3xl font-bold text-white">Connect, Collaborate, and Share Knowledge Effortlessly</h2>
            <p class="text-slate-300 text-lg">
              Our platform breaks down communication barriers, enabling teams to share insights, provide feedback, and collaborate in real-time.
            </p>
            
            <div class="space-y-6">
              <div class="flex items-start space-x-4">
                <div class="flex-shrink-0 w-10 h-10 bg-emerald-500/10 rounded-lg flex items-center justify-center">
                  <MessageCircle class="w-6 h-6 text-emerald-500" />
                </div>
                <div>
                  <h3 class="text-xl font-semibold text-white mb-2">Real-time Discussions</h3>
                  <p class="text-slate-300">Engage in threaded conversations around specific topics, with smart notifications to keep everyone in the loop.</p>
                </div>
              </div>
              
              <div class="flex items-start space-x-4">
                <div class="flex-shrink-0 w-10 h-10 bg-emerald-500/10 rounded-lg flex items-center justify-center">
                  <Share2 class="w-6 h-6 text-emerald-500" />
                </div>
                <div>
                  <h3 class="text-xl font-semibold text-white mb-2">Seamless Knowledge Sharing</h3>
                  <p class="text-slate-300">Share articles, tutorials, code snippets, and best practices with your team in just a few clicks.</p>
                </div>
              </div>
              
              <div class="flex items-start space-x-4">
                <div class="flex-shrink-0 w-10 h-10 bg-emerald-500/10 rounded-lg flex items-center justify-center">
                  <Users class="w-6 h-6 text-emerald-500" />
                </div>
                <div>
                  <h3 class="text-xl font-semibold text-white mb-2">Team Collaboration</h3>
                  <p class="text-slate-300">Work together on documents, provide feedback, and build upon each other's ideas in a collaborative environment.</p>
                </div>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Tech Topic Cards -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8 mt-32">
          <div class="group relative overflow-hidden rounded-xl bg-slate-800/50 backdrop-blur-sm border border-slate-700 hover:border-emerald-500/50 transition-all duration-300">
            <div class="absolute inset-0">
              <img 
                src="../assets/laptop.jpeg" 
                alt="Frontend Development"
                class="w-full h-full object-cover opacity-20 group-hover:opacity-30 transition-opacity duration-300"
              />
            </div>
            <div class="relative p-6">
              <Code class="w-8 h-8 text-emerald-500 mb-4" />
              <h3 class="text-xl font-semibold text-white mb-2">Frontend Development</h3>
              <p class="text-slate-300 mb-4">React, Vue, Angular, and modern web development practices.</p>
              <div class="flex flex-wrap gap-2">
                <span class="px-2 py-1 text-xs bg-emerald-500/10 text-emerald-500 rounded-full">React</span>
                <span class="px-2 py-1 text-xs bg-emerald-500/10 text-emerald-500 rounded-full">Vue</span>
                <span class="px-2 py-1 text-xs bg-emerald-500/10 text-emerald-500 rounded-full">TypeScript</span>
              </div>
            </div>
          </div>
  
          <div class="group relative overflow-hidden rounded-xl bg-slate-800/50 backdrop-blur-sm border border-slate-700 hover:border-emerald-500/50 transition-all duration-300">
            <div class="absolute inset-0">
              <img 
                src="../assets/server-image.jpeg" 
                alt="Backend Development"
                class="w-full h-full object-cover opacity-20 group-hover:opacity-30 transition-opacity duration-300"
              />
            </div>
            <div class="relative p-6">
              <Server class="w-8 h-8 text-emerald-500 mb-4" />
              <h3 class="text-xl font-semibold text-white mb-2">Backend Development</h3>
              <p class="text-slate-300 mb-4">Node.js, Python, databases, and API design patterns.</p>
              <div class="flex flex-wrap gap-2">
                <span class="px-2 py-1 text-xs bg-emerald-500/10 text-emerald-500 rounded-full">Node.js</span>
                <span class="px-2 py-1 text-xs bg-emerald-500/10 text-emerald-500 rounded-full">Python</span>
                <span class="px-2 py-1 text-xs bg-emerald-500/10 text-emerald-500 rounded-full">PostgreSQL</span>
              </div>
            </div>
          </div>
  
          <div class="group relative overflow-hidden rounded-xl bg-slate-800/50 backdrop-blur-sm border border-slate-700 hover:border-emerald-500/50 transition-all duration-300">
            <div class="absolute inset-0">
              <img 
                src="../assets/devOps.jpeg" 
                alt="DevOps"
                class="w-full h-full object-cover opacity-20 group-hover:opacity-30 transition-opacity duration-300"
              />
            </div>
            <div class="relative p-6">
              <Container class="w-8 h-8 text-emerald-500 mb-4" />
              <h3 class="text-xl font-semibold text-white mb-2">DevOps & Cloud</h3>
              <p class="text-slate-300 mb-4">Docker, Kubernetes, CI/CD, and cloud infrastructure.</p>
              <div class="flex flex-wrap gap-2">
                <span class="px-2 py-1 text-xs bg-emerald-500/10 text-emerald-500 rounded-full">Docker</span>
                <span class="px-2 py-1 text-xs bg-emerald-500/10 text-emerald-500 rounded-full">AWS</span>
                <span class="px-2 py-1 text-xs bg-emerald-500/10 text-emerald-500 rounded-full">CI/CD</span>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Features Showcase -->
        <div id="features" class="mt-32">
          <div class="text-center mb-16">
            <h2 class="text-3xl md:text-4xl font-bold text-white mb-4">Everything You Need to Excel</h2>
            <p class="text-slate-300 text-lg max-w-2xl mx-auto">
              A comprehensive platform designed to help your team share knowledge, collaborate, and grow together.
            </p>
          </div>
          
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <div class="bg-slate-800/50 backdrop-blur-sm rounded-xl border border-slate-700 p-6 hover:border-emerald-500/50 transition-all duration-300">
              <div class="w-12 h-12 bg-emerald-500/10 rounded-xl flex items-center justify-center mb-4">
                <MessageSquare class="w-6 h-6 text-emerald-500" />
              </div>
              <h3 class="text-xl font-semibold text-white mb-2">Smart Discussions</h3>
              <p class="text-slate-300">AI-powered discussions that automatically categorize and link related content for better knowledge discovery.</p>
            </div>
            
            <div class="bg-slate-800/50 backdrop-blur-sm rounded-xl border border-slate-700 p-6 hover:border-emerald-500/50 transition-all duration-300">
              <div class="w-12 h-12 bg-emerald-500/10 rounded-xl flex items-center justify-center mb-4">
                <Search class="w-6 h-6 text-emerald-500" />
              </div>
              <h3 class="text-xl font-semibold text-white mb-2">Semantic Search</h3>
              <p class="text-slate-300">Advanced search capabilities that understand context and relationships between content.</p>
            </div>
            
            <div class="bg-slate-800/50 backdrop-blur-sm rounded-xl border border-slate-700 p-6 hover:border-emerald-500/50 transition-all duration-300">
              <div class="w-12 h-12 bg-emerald-500/10 rounded-xl flex items-center justify-center mb-4">
                <BookOpen class="w-6 h-6 text-emerald-500" />
              </div>
              <h3 class="text-xl font-semibold text-white mb-2">Interactive Learning</h3>
              <p class="text-slate-300">Hands-on tutorials and interactive code examples that make learning engaging and effective.</p>
            </div>
            
            <div class="bg-slate-800/50 backdrop-blur-sm rounded-xl border border-slate-700 p-6 hover:border-emerald-500/50 transition-all duration-300">
              <div class="w-12 h-12 bg-emerald-500/10 rounded-xl flex items-center justify-center mb-4">
                <Users class="w-6 h-6 text-emerald-500" />
              </div>
              <h3 class="text-xl font-semibold text-white mb-2">Expert Network</h3>
              <p class="text-slate-300">Connect with industry experts and mentors who can guide your learning journey.</p>
            </div>
            
            <div class="bg-slate-800/50 backdrop-blur-sm rounded-xl border border-slate-700 p-6 hover:border-emerald-500/50 transition-all duration-300">
              <div class="w-12 h-12 bg-emerald-500/10 rounded-xl flex items-center justify-center mb-4">
                <Zap class="w-6 h-6 text-emerald-500" />
              </div>
              <h3 class="text-xl font-semibold text-white mb-2">Real-time Collaboration</h3>
              <p class="text-slate-300">Live coding sessions, pair programming, and collaborative document editing.</p>
            </div>
            
            <div class="bg-slate-800/50 backdrop-blur-sm rounded-xl border border-slate-700 p-6 hover:border-emerald-500/50 transition-all duration-300">
              <div class="w-12 h-12 bg-emerald-500/10 rounded-xl flex items-center justify-center mb-4">
                <Trophy class="w-6 h-6 text-emerald-500" />
              </div>
              <h3 class="text-xl font-semibold text-white mb-2">Gamified Learning</h3>
              <p class="text-slate-300">Achievement system that rewards contribution and learning milestones.</p>
            </div>
          </div>
        </div>
  
        <!-- Knowledge Sharing Impact -->
        <div class="mt-32 bg-slate-800/50 backdrop-blur-sm rounded-2xl border border-slate-700 p-8">
          <div class="text-center mb-12">
            <div class="inline-flex items-center px-4 py-2 bg-emerald-500/10 rounded-full mb-4">
              <BarChart class="w-5 h-5 text-emerald-500 mr-2" />
              <span class="text-emerald-500 font-medium">Impact Metrics</span>
            </div>
            <h2 class="text-3xl font-bold text-white mb-4">The Power of Knowledge Sharing</h2>
            <p class="text-slate-300 text-lg max-w-2xl mx-auto">
              See how organizations transform when they implement effective knowledge sharing practices.
            </p>
          </div>
          
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div class="relative">
              <div class="h-40 flex items-center justify-center">
                <!-- Circular Progress -->
                <div class="relative w-32 h-32">
                  <svg class="w-full h-full" viewBox="0 0 100 100">
                    <circle class="text-slate-700 stroke-current" stroke-width="10" cx="50" cy="50" r="40" fill="transparent"></circle>
                    <circle class="text-emerald-500 stroke-current" stroke-width="10" stroke-linecap="round" stroke-dasharray="251.2" stroke-dashoffset="50.24" cx="50" cy="50" r="40" fill="transparent">
                      <animate attributeName="stroke-dashoffset" from="251.2" to="50.24" dur="2s" fill="freeze" />
                    </circle>
                  </svg>
                  <div class="absolute inset-0 flex items-center justify-center">
                    <span class="text-3xl font-bold text-white">80%</span>
                  </div>
                </div>
              </div>
              <div class="text-center">
                <h3 class="text-xl font-semibold text-white mb-2">Knowledge Retention</h3>
                <p class="text-slate-400">Improved retention of critical information</p>
              </div>
            </div>
            
            <div class="relative">
              <div class="h-40 flex items-center justify-center">
                <!-- Circular Progress -->
                <div class="relative w-32 h-32">
                  <svg class="w-full h-full" viewBox="0 0 100 100">
                    <circle class="text-slate-700 stroke-current" stroke-width="10" cx="50" cy="50" r="40" fill="transparent"></circle>
                    <circle class="text-emerald-500 stroke-current" stroke-width="10" stroke-linecap="round" stroke-dasharray="251.2" stroke-dashoffset="125.6" cx="50" cy="50" r="40" fill="transparent">
                      <animate attributeName="stroke-dashoffset" from="251.2" to="125.6" dur="2s" fill="freeze" />
                    </circle>
                  </svg>
                  <div class="absolute inset-0 flex items-center justify-center">
                    <span class="text-3xl font-bold text-white">50%</span>
                  </div>
                </div>
              </div>
              <div class="text-center">
                <h3 class="text-xl font-semibold text-white mb-2">Faster Problem Solving</h3>
                <p class="text-slate-400">Reduced time to resolve technical issues</p>
              </div>
            </div>
            
            <div class="relative">
              <div class="h-40 flex items-center justify-center">
                <!-- Circular Progress -->
                <div class="relative w-32 h-32">
                  <svg class="w-full h-full" viewBox="0 0 100 100">
                    <circle class="text-slate-700 stroke-current" stroke-width="10" cx="50" cy="50" r="40" fill="transparent"></circle>
                    <circle class="text-emerald-500 stroke-current" stroke-width="10" stroke-linecap="round" stroke-dasharray="251.2" stroke-dashoffset="75.36" cx="50" cy="50" r="40" fill="transparent">
                      <animate attributeName="stroke-dashoffset" from="251.2" to="75.36" dur="2s" fill="freeze" />
                    </circle>
                  </svg>
                  <div class="absolute inset-0 flex items-center justify-center">
                    <span class="text-3xl font-bold text-white">70%</span>
                  </div>
                </div>
              </div>
              <div class="text-center">
                <h3 class="text-xl font-semibold text-white mb-2">Team Collaboration</h3>
                <p class="text-slate-400">Increased cross-team collaboration</p>
              </div>
            </div>
            
            <div class="relative">
              <div class="h-40 flex items-center justify-center">
                <!-- Circular Progress -->
                <div class="relative w-32 h-32">
                  <svg class="w-full h-full" viewBox="0 0 100 100">
                    <circle class="text-slate-700 stroke-current" stroke-width="10" cx="50" cy="50" r="40" fill="transparent"></circle>
                    <circle class="text-emerald-500 stroke-current" stroke-width="10" stroke-linecap="round" stroke-dasharray="251.2" stroke-dashoffset="62.8" cx="50" cy="50" r="40" fill="transparent">
                      <animate attributeName="stroke-dashoffset" from="251.2" to="62.8" dur="2s" fill="freeze" />
                    </circle>
                  </svg>
                  <div class="absolute inset-0 flex items-center justify-center">
                    <span class="text-3xl font-bold text-white">75%</span>
                  </div>
                </div>
              </div>
              <div class="text-center">
                <h3 class="text-xl font-semibold text-white mb-2">Employee Satisfaction</h3>
                <p class="text-slate-400">Higher job satisfaction and engagement</p>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Community Section -->
        <div class="mt-32 grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div class="space-y-8">
            <div class="inline-flex items-center px-4 py-2 bg-emerald-500/10 rounded-full">
              <Users class="w-5 h-5 text-emerald-500 mr-2" />
              <span class="text-emerald-500 font-medium">Vibrant Community</span>
            </div>
            <h2 class="text-3xl font-bold text-white">Join a Global Network of Tech Professionals</h2>
            <div class="space-y-6">
              <div class="flex items-start space-x-4">
                <div class="flex-shrink-0 w-10 h-10 bg-emerald-500/10 rounded-lg flex items-center justify-center">
                  <Users class="w-6 h-6 text-emerald-500" />
                </div>
                <div>
                  <h3 class="text-xl font-semibold text-white mb-2">Collaborative Learning</h3>
                  <p class="text-slate-300">Learn from peers, share experiences, and grow together in a supportive environment.</p>
                </div>
              </div>
              <div class="flex items-start space-x-4">
                <div class="flex-shrink-0 w-10 h-10 bg-emerald-500/10 rounded-lg flex items-center justify-center">
                  <Lightbulb class="w-6 h-6 text-emerald-500" />
                </div>
                <div>
                  <h3 class="text-xl font-semibold text-white mb-2">Knowledge Exchange</h3>
                  <p class="text-slate-300">Share insights, best practices, and learn from real-world experiences of industry experts.</p>
                </div>
              </div>
              <div class="flex items-start space-x-4">
                <div class="flex-shrink-0 w-10 h-10 bg-emerald-500/10 rounded-lg flex items-center justify-center">
                  <Target class="w-6 h-6 text-emerald-500" />
                </div>
                <div>
                  <h3 class="text-xl font-semibold text-white mb-2">Career Growth</h3>
                  <p class="text-slate-300">Accelerate your professional development through mentorship and networking opportunities.</p>
                </div>
              </div>
            </div>
          </div>
          <div class="relative">
            <div class="relative rounded-2xl overflow-hidden">
              <img 
                src="../assets/login-bg.jpeg"
                alt="Team Collaboration"
                class="w-full h-[600px] object-cover rounded-2xl"
              />
              <div class="absolute inset-0 bg-gradient-to-t from-slate-900 via-slate-900/20 to-transparent"></div>
            </div>
            <!-- Floating Stats -->
            <div class="absolute bottom-8 left-8 right-8">
              <div class="grid grid-cols-2 gap-4">
                <div class="bg-slate-800/90 backdrop-blur-sm rounded-xl p-4">
                  <div class="text-2xl font-bold text-emerald-500">5000+</div>
                  <div class="text-slate-300 text-sm">Active Members</div>
                </div>
                <div class="bg-slate-800/90 backdrop-blur-sm rounded-xl p-4">
                  <div class="text-2xl font-bold text-emerald-500">98%</div>
                  <div class="text-slate-300 text-sm">Satisfaction Rate</div>
                </div>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Latest Articles Preview -->
        <div class="mt-32">
          <div class="flex justify-between items-center mb-8">
            <div>
              <div class="inline-flex items-center px-4 py-2 bg-emerald-500/10 rounded-full mb-4">
                <BookOpen class="w-5 h-5 text-emerald-500 mr-2" />
                <span class="text-emerald-500 font-medium">Latest Updates</span>
              </div>
              <h2 class="text-3xl font-bold text-white">Featured Knowledge Shares</h2>
            </div>
            <router-link to="/feed" class="text-emerald-500 hover:text-emerald-400 flex items-center">
              View All
              <ArrowRight class="w-4 h-4 ml-1" />
            </router-link>
          </div>
  
          <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div v-for="article in latestArticles" :key="article.id"
              class="group bg-slate-800/50 backdrop-blur-sm rounded-lg border border-slate-700 hover:border-emerald-500/50 transition-all duration-300 p-6">
              <div class="flex items-center space-x-2 mb-4">
                <span :class="article.tagColor" class="px-2 py-1 text-xs rounded-full">
                  {{ article.tag }}
                </span>
                <span class="text-slate-400 text-sm">{{ article.date }}</span>
              </div>
              <h3 class="text-lg font-semibold text-white mb-2 line-clamp-2 group-hover:text-emerald-500 transition-colors">
                {{ article.title }}
              </h3>
              <p class="text-slate-400 text-sm mb-4 line-clamp-2">{{ article.excerpt }}</p>
              <div class="flex items-center justify-between">
                <div class="flex items-center space-x-2">
                  <div class="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center">
                    <span class="text-white text-sm">{{ article.authorInitials }}</span>
                  </div>
                  <span class="text-sm text-slate-300">{{ article.author }}</span>
                </div>
                <div class="flex items-center text-slate-400 text-sm">
                  <Clock class="w-4 h-4 mr-1" />
                  <span>{{ article.readTime }} min read</span>
                </div>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Call to Action -->
        <div class="mt-32 text-center">
          <div class="max-w-3xl mx-auto">
            <h2 class="text-3xl md:text-4xl font-bold text-white mb-6">
              Ready to Transform Your Team's Knowledge Sharing?
            </h2>
            <p class="text-slate-300 text-lg mb-8">
              Join thousands of professionals who are already leveraging our platform to build stronger, more knowledgeable teams.
            </p>
            <router-link to="/login" 
              class="inline-flex items-center px-8 py-4 bg-emerald-500 text-white rounded-lg text-lg font-medium hover:bg-emerald-600 transform hover:scale-105 transition-all duration-200 shadow-lg hover:shadow-emerald-500/20">
              Get Started Now
              <ArrowRight class="ml-2 w-6 h-6" />
            </router-link>
          </div>
        </div>
  
        <!-- Trust Indicators -->
        <div class="mt-32 bg-slate-800/50 backdrop-blur-sm rounded-2xl border border-slate-700 p-8">
          <div class="grid grid-cols-2 md:grid-cols-4 gap-8">
            <div class="text-center">
              <div class="text-3xl font-bold text-emerald-500 mb-2">5000+</div>
              <div class="text-slate-300">Active Members</div>
            </div>
            <div class="text-center">
              <div class="text-3xl font-bold text-emerald-500 mb-2">1200+</div>
              <div class="text-slate-300">Knowledge Articles</div>
            </div>
            <div class="text-center">
              <div class="text-3xl font-bold text-emerald-500 mb-2">98%</div>
              <div class="text-slate-300">Member Satisfaction</div>
            </div>
            <div class="text-center">
              <div class="text-3xl font-bold text-emerald-500 mb-2">24/7</div>
              <div class="text-slate-300">Community Support</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  
    <!-- Footer -->
    <footer class="border-t border-slate-800 mt-32">
      <div class="max-w-[1440px] mx-auto px-4 sm:px-6 lg:px-4 py-12">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div class="space-y-4">
            <div class="flex items-center">
              <BookOpen class="w-8 h-8 text-emerald-500" />
              <span class="ml-2 text-xl font-bold text-white">Imbus Knowledge</span>
            </div>
            <p class="text-slate-400">
              Empowering our teams through collaborative knowledge sharing and continuous learning.
            </p>
            <div class="flex items-center space-x-2 text-slate-400">
              <LockKeyhole class="w-4 h-4" />
              <span class="text-sm">Internal company platform</span>
            </div>
          </div>
          <div>
            <h4 class="text-white font-semibold mb-4">Resources</h4>
            <ul class="space-y-2">
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">Documentation</a></li>
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">Tutorials</a></li>
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">Knowledge Base</a></li>
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">Tech Support</a></li>
            </ul>
          </div>
          <div>
            <h4 class="text-white font-semibold mb-4">Departments</h4>
            <ul class="space-y-2">
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">Engineering</a></li>
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">Product</a></li>
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">Design</a></li>
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">Marketing</a></li>
            </ul>
          </div>
          <div>
            <h4 class="text-white font-semibold mb-4">Company</h4>
            <ul class="space-y-2">
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">Intranet</a></li>
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">HR Portal</a></li>
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">IT Help Desk</a></li>
              <li><a href="#" class="text-slate-400 hover:text-white transition-colors">Directory</a></li>
            </ul>
          </div>
        </div>
        <div class="mt-8 pt-8 border-t border-slate-700 flex flex-col md:flex-row justify-between items-center">
          <div class="text-slate-400 text-sm">
            © 2025 Imbus Technologies. For internal use only.
          </div>
          <div class="flex items-center space-x-4 mt-4 md:mt-0">
            <div class="text-slate-400 text-sm flex items-center">
              <HelpCircle class="w-4 h-4 mr-1" />
              <span>IT Support: ext. 4567</span>
            </div>
            <div class="text-slate-400 text-sm flex items-center">
              <Mail class="w-4 h-4 mr-1" />
              <span>it-support@imbus-internal.com</span>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </template>
  
  <script setup lang="ts">
  import { 
    ArrowRight, BookOpen, Code, Server, Container,
    Users, Lightbulb, Target, MessageSquare, Clock,
    Search, Zap, Trophy, Shield, Award,
    Sparkles,TrendingUp, BarChart, MessageCircle, Share2,
    LockKeyhole, HelpCircle, Mail
  } from 'lucide-vue-next';
  
  const latestArticles = [
    {
      id: 1,
      title: "Implementing Micro-frontends with Module Federation",
      excerpt: "A comprehensive guide to scaling frontend applications using Webpack 5's Module Federation.",
      tag: "Frontend",
      tagColor: "bg-blue-500/10 text-blue-500",
      author: "Alex Chen",
      authorInitials: "AC",
      date: "2h ago",
      readTime: 8
    },
    {
      id: 2,
      title: "Building Scalable APIs with GraphQL and Node.js",
      excerpt: "Best practices for designing and implementing GraphQL APIs in a microservices architecture.",
      tag: "Backend",
      tagColor: "bg-purple-500/10 text-purple-500",
      author: "Sarah Kim",
      authorInitials: "SK",
      date: "4h ago",
      readTime: 12
    },
    {
      id: 3,
      title: "Kubernetes Deployment Strategies Explained",
      excerpt: "A deep dive into different deployment strategies in Kubernetes and when to use them.",
      tag: "DevOps",
      tagColor: "bg-emerald-500/10 text-emerald-500",
      author: "Mike Johnson",
      authorInitials: "MJ",
      date: "6h ago",
      readTime: 15
    }
  ];
  </script>